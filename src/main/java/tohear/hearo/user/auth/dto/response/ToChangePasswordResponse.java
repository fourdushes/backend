package tohear.hearo.user.auth.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.auth.domain.UserType;

@Data
@NoArgsConstructor
public class ToChangePasswordResponse {

    private String id;
    private UserType userType;
    private String tempToken;

    public ToChangePasswordResponse(String id, UserType userType, String tempToken) {
        this.id = id;
        this.userType = userType;
        this.tempToken = tempToken;
    }

}
