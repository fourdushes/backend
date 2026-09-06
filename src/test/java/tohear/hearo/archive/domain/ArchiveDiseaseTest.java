package tohear.hearo.archive.domain;

import org.junit.jupiter.api.Test;

import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.ward.WardUser;

import static org.assertj.core.api.Assertions.assertThat;

class ArchiveDiseaseTest {

    @Test
    void archiveCreatesAnUnclassifiedDiseaseRecordWithBothSidesConnected() {
        WardUser ward = new WardUser("ward", "환자", "ward@test.com", "pw", UserType.WARD);
        Archive archive = new Archive("", ward);

        assertThat(archive.getArchiveDisease()).isNotNull();
        assertThat(archive.getArchiveDisease().getArchive()).isSameAs(archive);
        assertThat(archive.getArchiveDisease().getArchive().getWardUser()).isSameAs(ward);
        assertThat(archive.getArchiveDisease().getDiseaseType()).isNull();
    }

    @Test
    void classificationUpdatesTheExistingRecordInsteadOfCreatingAnother() {
        Archive archive = new Archive("",
                new WardUser("ward", "환자", "ward@test.com", "pw", UserType.WARD));
        ArchiveDisease disease = archive.getArchiveDisease();

        archive.updateDisease(" COLD ");

        assertThat(archive.getArchiveDisease()).isSameAs(disease);
        assertThat(disease.getDiseaseType()).isEqualTo(DiseaseType.COLD);
        assertThat(disease.getDiseaseWord()).isEqualTo("COLD");

        archive.updateDisease("UNDEFINED_8");
        assertThat(archive.getArchiveDisease()).isSameAs(disease);
        assertThat(disease.getDiseaseType()).isEqualTo(DiseaseType.UNDEFINED_8);
    }

    @Test
    void koreanDiseaseIsClassifiedAndOriginalWordIsPreserved() {
        Archive archive = new Archive();

        archive.updateDisease(" 감기 ");

        assertThat(archive.getArchiveDisease().getDiseaseType()).isEqualTo(DiseaseType.COLD);
        assertThat(archive.getArchiveDisease().getDiseaseWord()).isEqualTo("감기");

        archive.updateDisease("암");

        assertThat(archive.getArchiveDisease().getDiseaseType()).isEqualTo(DiseaseType.OTHER);
        assertThat(archive.getArchiveDisease().getDiseaseWord()).isEqualTo("암");
    }

    @Test
    void legacyArchiveCreatesDiseaseRecordWhenClassified() {
        Archive archive = new Archive();
        assertThat(archive.getArchiveDisease()).isNull();

        archive.updateDisease("COLD");

        assertThat(archive.getArchiveDisease().getArchive()).isSameAs(archive);
        assertThat(archive.getArchiveDisease().getDiseaseType()).isEqualTo(DiseaseType.COLD);
    }
}
