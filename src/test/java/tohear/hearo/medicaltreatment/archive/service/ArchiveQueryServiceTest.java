package tohear.hearo.medicaltreatment.archive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import tohear.hearo.medicaltreatment.archive.domain.Archive;
import tohear.hearo.medicaltreatment.archive.reposiotry.ArchiveRepository;
import tohear.hearo.medicaltreatment.auth.MedicalUserPrincipal;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.ward.WardUser;

@ExtendWith(MockitoExtension.class)
class ArchiveQueryServiceTest {

    @Mock
    ArchiveRepository archiveRepository;

    private ArchiveQueryService archiveQueryService;

    @BeforeEach
    void setUp() {
        archiveQueryService = new ArchiveQueryService(archiveRepository);
    }

    @Test
    void archiveOwnerCanReadArchive() {
        Archive archive = archive();
        when(archiveRepository.findById(1L)).thenReturn(Optional.of(archive));

        var response = archiveQueryService.findArchive(
                1L, new MedicalUserPrincipal("ward", UserType.WARD));

        assertThat(response.getArchiveId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo(archive.getTitle());
    }

    @Test
    void otherWardCannotReadArchive() {
        when(archiveRepository.findById(1L)).thenReturn(Optional.of(archive()));

        assertThatThrownBy(() -> archiveQueryService.findArchive(
                1L, new MedicalUserPrincipal("other", UserType.WARD)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 진료 기록을 조회할 권한이 없습니다.");
    }

    @Test
    void institutionUserCannotReadWardArchive() {
        assertThatThrownBy(() -> archiveQueryService.findArchive(
                1L, new MedicalUserPrincipal("doctor", UserType.INSTITUTIONS)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("피보호자만 진료 기록을 조회할 수 있습니다.");
    }

    private Archive archive() {
        WardUser ward = new WardUser("ward", "환자", "ward@test.com", "pw", UserType.WARD);
        Archive archive = new Archive("진료 내용", ward);
        archive.updateAllChatText("상대: 어디가 불편해서 오셨나요?\n나: 목이 아파요.");
        ReflectionTestUtils.setField(archive, "id", 1L);
        return archive;
    }
}
