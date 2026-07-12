package tohear.hearo.care.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.care.domain.CareState;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeCareStateResponse {

    private Long careId;
    private CareState careState;

}
