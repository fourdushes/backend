package tohear.hearo.user.auth.dto.response;

import lombok.Data;
import tohear.hearo.user.auth.domain.UserType;

@Data
public class ToChangePasswordResponse {

    private String id;
    private UserType userType;

    public ToChangePasswordResponse(String id, UserType userType) {
        this.id = id;
        this.userType = userType;
    }

}
