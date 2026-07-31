package tohear.hearo.care.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.auth.domain.UserType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardSearchDto {

    private long careId;
    private String guardUserId;
    private String guardUserName;
    private UserType userType;
    private boolean mainGuardUser;
}
