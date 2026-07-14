package tohear.hearo.medicaltreatment.chat.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import tohear.hearo.medicaltreatment.archive.domain.Archive;
import tohear.hearo.medicaltreatment.medicalrequest.domain.MedicalRequest;
import tohear.hearo.user.institution.InstitutionsUser;
import tohear.hearo.user.ward.WardUser;

@Entity
@Getter
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_request_id", nullable = false, unique = true)
    private MedicalRequest medicalRequest;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "archive_id", nullable = false, unique = true)
    private Archive archive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institutions_user_id", nullable = false)
    private InstitutionsUser institutionUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_user_id", nullable = false)
    private WardUser wardUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    protected ChatRoom() {
    }

    public ChatRoom(MedicalRequest medicalRequest, Archive archive) {
        this.medicalRequest = medicalRequest;
        this.archive = archive;
        this.institutionUser = medicalRequest.getInstitutionUser();
        this.wardUser = medicalRequest.getWardUser();
        this.status = ChatRoomStatus.IN_PROGRESS;
        this.createdAt = LocalDateTime.now();
        this.startedAt = LocalDateTime.now();
    }

    public void validateInProgress() {
        if (status != ChatRoomStatus.IN_PROGRESS) {
            throw new IllegalStateException("이미 종료된 진료입니다.");
        }
    }

    public void complete() {
        validateInProgress();
        status = ChatRoomStatus.COMPLETED;
        completedAt = LocalDateTime.now();
    }
}
