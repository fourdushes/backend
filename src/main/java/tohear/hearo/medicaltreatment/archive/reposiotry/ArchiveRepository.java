package tohear.hearo.medicaltreatment.archive.reposiotry;

import org.springframework.data.jpa.repository.JpaRepository;

import tohear.hearo.medicaltreatment.archive.domain.Archive;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

}
