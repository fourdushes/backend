package tohear.hearo.medicaltreatment.medicalrequest.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.medicaltreatment.medicalrequest.domain.MedicalRequestStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRequestResponse {

    private Long medicalRequestId;
    private String wardUserId;
    private String wardUserName;
    private String institutionUserId;
    private String institutionUserName;
    private MedicalRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
