package tohear.hearo.medicaltreatment.record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteRecordResponse {

    private String recordWord; // 텍스트화 된 레코드
    private String recordDate;
    
}
