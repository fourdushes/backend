package tohear.hearo.user.auth.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.auth.domain.UserType;

@Data
@NoArgsConstructor
public class ChangePasswordRequest {

    private String id;
    private String newPassword;
    private String checkNewPassword;
    private UserType userType;
    private String tempToken; // 임시 토큰 추가
    

}
