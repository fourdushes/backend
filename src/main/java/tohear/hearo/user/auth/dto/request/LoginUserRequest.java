package tohear.hearo.user.auth.dto.request;

import lombok.Data;

@Data
public class LoginUserRequest {

    private String id;
    private String password;

}
