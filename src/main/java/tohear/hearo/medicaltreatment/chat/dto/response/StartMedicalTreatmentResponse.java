package tohear.hearo.medicaltreatment.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartMedicalTreatmentResponse {

    private Long chatRoomId;
    private Long archiveId;
}
