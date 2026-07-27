package tohear.hearo.inquiry.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tohear.hearo.inquiry.domain.InquiryStatus;

@Getter
@AllArgsConstructor
public class InquiryResponse {

    private Long inquiryId;
    private String userId;
    private String userName;
    private tohear.hearo.user.auth.domain.UserType userType;
    private String title;
    private String content;
    private InquiryStatus status;
    private String answer;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
}
