package tohear.hearo.medicaltreatment.medicalrequest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import tohear.hearo.medicaltreatment.medicalrequest.domain.MedicalRequest;
import tohear.hearo.medicaltreatment.medicalrequest.domain.MedicalRequestStatus;

public interface MedicalRequestRepository extends JpaRepository<MedicalRequest, Long> {

    boolean existsByWardUserIdAndInstitutionUserIdAndStatusIn(
            String wardUserId, String institutionUserId, List<MedicalRequestStatus> statuses);

    @EntityGraph(attributePaths = {"wardUser", "institutionUser"})
    List<MedicalRequest> findAllByWardUserIdOrderByCreatedAtDesc(String wardUserId);

    @EntityGraph(attributePaths = {"wardUser", "institutionUser"})
    List<MedicalRequest> findAllByInstitutionUserIdOrderByCreatedAtDesc(String institutionUserId);

    @EntityGraph(attributePaths = {"wardUser", "institutionUser"})
    @Query("select mr from MedicalRequest mr where mr.id = :id")
    Optional<MedicalRequest> findDetailById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"wardUser", "institutionUser"})
    @Query("select mr from MedicalRequest mr where mr.id = :id")
    Optional<MedicalRequest> findByIdForUpdate(@Param("id") Long id);
}
