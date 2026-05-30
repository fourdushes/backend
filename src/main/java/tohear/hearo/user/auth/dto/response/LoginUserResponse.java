package tohear.hearo.user.auth.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.auth.domain.UserType;

@Data
@NoArgsConstructor
public class LoginUserResponse {

    private String accessToken;
    private String userId;
    private UserType userType;

    public LoginUserResponse(String accessToken, String userId, UserType userType) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.userType = userType;
    }

}
