package tohear.hearo.medicaltreatment.archive.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tohear.hearo.medicaltreatment.archive.domain.Archive;
import tohear.hearo.medicaltreatment.archive.reposiotry.ArchiveRepository;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final ArchiveRepository archiveRepository;

    public void save(Archive archive) {
        archiveRepository.save(archive);
    }

    public Archive findById(Long archiveId) {
        return archiveRepository.findById(archiveId).orElseThrow(() -> new IllegalArgumentException("저장된 파일을 찾을 수 없습니다."));
    }

}
