package tohear.hearo.institution.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import tohear.hearo.institution.domain.Institution;

public interface InstitutionRepository extends JpaRepository<Institution, Long>, InstitutionRepositoryCustom {

    Page<Institution> findByInstitutionNameContainingIgnoreCase(String name, Pageable pageable);
}
