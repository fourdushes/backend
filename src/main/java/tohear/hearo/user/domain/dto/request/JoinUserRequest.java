package tohear.hearo.user.domain.dto.request;

import lombok.Data;
import tohear.hearo.user.domain.UserType;

@Data
public class JoinUserRequest {

    private String id;
    private String name;
    private String email;
    private String password;
    private UserType userType; // 사용자 유형 (피보호자, 보호자)

}
