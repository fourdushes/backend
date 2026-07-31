package tohear.hearo.institution.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tohear.hearo.institution.domain.Institution;

public interface InstitutionRepository extends JpaRepository<Institution, Long>, InstitutionRepositoryCustom {

    Page<Institution> findByInstitutionNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByInstitutionLoginId(String loginId);
    Optional<Institution> findByInstitutionLoginId(String loginId);
    boolean existsByEmail(String standardEmail);
    Optional<Institution> findByInstitutionNameAndEmailIgnoreCase(String institutionName, String email);
    Optional<Institution> findByEmailIgnoreCase(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Institution institution set institution.passward = :password where institution.id = :institutionId")
    int updatePassword(@Param("institutionId") Long institutionId, @Param("password") String password);
}
