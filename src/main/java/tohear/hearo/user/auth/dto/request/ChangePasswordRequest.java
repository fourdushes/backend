package tohear.hearo.user.auth.dto.request;

import lombok.Data;
import tohear.hearo.user.auth.domain.UserType;

@Data
public class ChangePasswordRequest {

    private String id;
    private String newPassword;
    private String checkNewPassword;
    private UserType userType;
    

}
