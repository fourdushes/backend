package tohear.hearo.medicaltreatment.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import tohear.hearo.medicaltreatment.chat.domain.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    boolean existsByMedicalRequestId(Long medicalRequestId);

    @EntityGraph(attributePaths = {"medicalRequest", "archive", "institutionUser", "wardUser"})
    @Query("select cr from ChatRoom cr where cr.id = :id")
    Optional<ChatRoom> findDetailById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"medicalRequest", "archive", "institutionUser", "wardUser"})
    @Query("select cr from ChatRoom cr where cr.id = :id")
    Optional<ChatRoom> findByIdForUpdate(@Param("id") Long id);

}
