package tohear.hearo.user.auth.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {

    private String id;
    private String currentPassword;
    private String newPassword;

}
