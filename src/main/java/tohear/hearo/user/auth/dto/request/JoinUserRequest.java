package tohear.hearo.user.auth.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.auth.domain.UserType;

@Data
@NoArgsConstructor
public class JoinUserRequest {

    private String id;
    private String name;
    private String email;
    private String password;
    private UserType userType; // 사용자 유형 (피보호자, 보호자)

}
