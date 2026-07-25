package tohear.hearo.care.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckMainGuardRequest {

    private String wardUserId; // 메인 보호자를 설정하는 피보호자
    private String changeGuardUserId; // 변경될 메인 보호자


}
