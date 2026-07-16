package tohear.hearo.user.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.auth.domain.UserType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserResponse {

    private String accessToken;
    private String userId;
    private UserType userType;
    private String refreshToken;

}
