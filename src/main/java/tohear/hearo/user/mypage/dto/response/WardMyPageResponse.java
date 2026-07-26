package tohear.hearo.user.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.auth.domain.UserType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WardMyPageResponse {

    private String userId;
    private String username;
    private String email;
    private UserType userType;

}
