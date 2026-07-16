package tohear.hearo.archive.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tohear.hearo.archive.domain.Archive;
import tohear.hearo.archive.dto.response.ArchiveResponse;
import tohear.hearo.archive.repository.ArchiveRepository;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;
import tohear.hearo.user.auth.domain.UserType;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveQueryService {

    private final ArchiveRepository archiveRepository;

    public ArchiveResponse findArchive(Long archiveId, MedicalUserPrincipal principal) {
        validateWardUser(principal);

        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new IllegalArgumentException("진료 기록을 찾을 수 없습니다."));

        validateArchiveOwner(archive, principal);

        return new ArchiveResponse(
                archive.getId(),
                archive.getTitle(),
                archive.getArchiveDate(),
                archive.getText(),
                archive.getAllChatText());
    }

    private void validateWardUser(MedicalUserPrincipal principal) {
        if (principal.getUserType() != UserType.WARD) {
            throw new IllegalArgumentException("피보호자만 진료 기록을 조회할 수 있습니다.");
        }
    }

    private void validateArchiveOwner(Archive archive, MedicalUserPrincipal principal) {
        if (!archive.getWardUser().getId().equals(principal.getUserId())) {
            throw new IllegalArgumentException("해당 진료 기록을 조회할 권한이 없습니다.");
        }
    }
}
