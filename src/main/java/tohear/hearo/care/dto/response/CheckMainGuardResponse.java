package tohear.hearo.care.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckMainGuardResponse {

    private Long deleteMainCare; // 변경 전 메인 보호자
    private Long changeMainCare; // 변경 후 메인 보호자

}
