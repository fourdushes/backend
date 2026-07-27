package tohear.hearo.inquiry.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import tohear.hearo.user.auth.domain.UserType;

@Entity
@Getter
@Table(indexes = {
    @Index(name = "idx_inquiry_user_created", columnList = "user_id,user_type,created_at")
})
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 30)
    private UserType userType;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InquiryStatus status;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime answeredAt;

    protected Inquiry() {
    }

    public Inquiry(String userId, UserType userType, String title, String content) {
        this.userId = userId;
        this.userType = userType;
        this.title = title;
        this.content = content;
        this.status = InquiryStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void answer(String answer) {
        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("답변 내용을 입력해 주세요.");
        }
        this.answer = answer.trim();
        this.status = InquiryStatus.ANSWERED;
        this.answeredAt = LocalDateTime.now();
    }
}
