package tohear.hearo.care.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

// 이것도 안씀 삭제 가능
public class GuardCheckCareListRequest {

    private String guardUserId; // 보호자 아이디

}
