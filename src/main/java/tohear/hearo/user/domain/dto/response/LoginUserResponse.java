package tohear.hearo.user.domain.dto.response;

import lombok.Data;

@Data
public class LoginUserResponse {

    private String accessToken;
    private String userId;

    public LoginUserResponse(String accessToken, String userId) {
        this.accessToken = accessToken;
        this.userId = userId;
    }

}
