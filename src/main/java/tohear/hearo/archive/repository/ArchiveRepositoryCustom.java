package tohear.hearo.archive.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tohear.hearo.archive.domain.Archive;

public interface ArchiveRepositoryCustom {

    Page<Archive> findAllByUserId(String userId, Pageable pageable);

}
