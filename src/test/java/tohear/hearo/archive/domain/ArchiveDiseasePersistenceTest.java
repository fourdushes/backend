package tohear.hearo.archive.domain;

import java.util.UUID;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.ward.WardUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** 실제 서비스 DB 대신 테스트 전용 인메모리 DB에서 cascade/제약/롤백을 검증한다. */
class ArchiveDiseasePersistenceTest {

    private static SessionFactory sessionFactory;

    @BeforeAll
    static void setUpDatabase() {
        var registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.connection.url", "jdbc:h2:mem:archive_disease_test;DB_CLOSE_DELAY=-1")
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                .build();
        try {
            sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClass(WardUser.class)
                    .addAnnotatedClass(Archive.class)
                    .addAnnotatedClass(ArchiveDisease.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (RuntimeException e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }

    @AfterAll
    static void closeDatabase() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void savingArchiveCascadesToOneUnclassifiedDiseaseRecord() {
        Long archiveId = persistArchive(false);

        inTransaction(session -> {
            Archive archive = session.find(Archive.class, archiveId);
            assertThat(archive.getArchiveDisease().getId()).isNotNull();
            assertThat(archive.getArchiveDisease().getDiseaseType()).isNull();
            assertThat(archive.getArchiveDisease().getArchive()).isSameAs(archive);
            assertThat(archive.getArchiveDisease().getArchive().getWardUser().getId()).isNotBlank();
            assertThat(countDiseaseRows(session, archiveId)).isEqualTo(1);
            return null;
        });
    }

    @Test
    void dirtyCheckingUpdatesDiseaseWithoutExplicitSave() {
        Long archiveId = persistArchive(false);
        Long diseaseId = inTransaction(session -> {
            Archive archive = session.find(Archive.class, archiveId);
            archive.updateDisease("COLD");
            return archive.getArchiveDisease().getId();
        });

        inTransaction(session -> {
            ArchiveDisease disease = session.find(Archive.class, archiveId).getArchiveDisease();
            assertThat(disease.getId()).isEqualTo(diseaseId);
            assertThat(disease.getDiseaseType()).isEqualTo(DiseaseType.COLD);
            assertThat(disease.getDiseaseWord()).isEqualTo("COLD");
            assertThat(countDiseaseRows(session, archiveId)).isEqualTo(1);
            return null;
        });
    }

    @Test
    void legacyArchiveCanCascadeNewDiseaseOnFlush() {
        Long archiveId = persistArchive(true);
        inTransaction(session -> {
            Archive archive = session.find(Archive.class, archiveId);
            assertThat(archive.getArchiveDisease()).isNull();
            archive.updateDisease("UNDEFINED_1");
            return null;
        });

        inTransaction(session -> {
            assertThat(session.find(Archive.class, archiveId).getArchiveDisease().getDiseaseType())
                    .isEqualTo(DiseaseType.UNDEFINED_1);
            assertThat(countDiseaseRows(session, archiveId)).isEqualTo(1);
            return null;
        });
    }

    @Test
    void classificationIsRolledBackOnFailure() {
        Long archiveId = persistArchive(false);

        assertThatThrownBy(() -> inTransaction(session -> {
            session.find(Archive.class, archiveId).updateDisease("COLD");
            session.flush();
            throw new IllegalStateException("진료 완료 저장 실패");
        })).isInstanceOf(IllegalStateException.class);

        inTransaction(session -> {
            ArchiveDisease disease = session.find(Archive.class, archiveId).getArchiveDisease();
            assertThat(disease.getDiseaseType()).isNull();
            assertThat(disease.getDiseaseWord()).isNull();
            return null;
        });
    }

    @Test
    void duplicateDiseaseRecordForOneArchiveIsRejectedByDatabase() {
        Long archiveId = persistArchive(false);

        assertThatThrownBy(() -> inTransaction(session -> {
            session.persist(new ArchiveDisease(session.find(Archive.class, archiveId)));
            session.flush();
            return null;
        })).isInstanceOf(jakarta.persistence.PersistenceException.class);

        inTransaction(session -> {
            assertThat(countDiseaseRows(session, archiveId)).isEqualTo(1);
            return null;
        });
    }

    @Test
    void deletingArchiveCascadesToDiseaseButKeepsWardUser() {
        Long archiveId = persistArchive(false);
        String wardId = inTransaction(session -> {
            Archive archive = session.find(Archive.class, archiveId);
            String id = archive.getWardUser().getId();
            session.remove(archive);
            return id;
        });

        inTransaction(session -> {
            assertThat(session.find(Archive.class, archiveId)).isNull();
            assertThat(countDiseaseRows(session, archiveId)).isZero();
            assertThat(session.find(WardUser.class, wardId)).isNotNull();
            return null;
        });
    }

    private Long persistArchive(boolean legacy) {
        return inTransaction(session -> {
            WardUser ward = new WardUser(UUID.randomUUID().toString(), "환자", "ward@test.com", "pw", UserType.WARD);
            session.persist(ward);
            Archive archive = new Archive("", ward);
            if (legacy) {
                ReflectionTestUtils.setField(archive, "archiveDisease", null);
            }
            session.persist(archive);
            return archive.getId();
        });
    }

    private long countDiseaseRows(Session session, Long archiveId) {
        return session.createSelectionQuery(
                        "select count(d) from ArchiveDisease d where d.archive.id = :archiveId", Long.class)
                .setParameter("archiveId", archiveId)
                .getSingleResult();
    }

    private <T> T inTransaction(Function<Session, T> work) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                T result = work.apply(session);
                transaction.commit();
                return result;
            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
}
