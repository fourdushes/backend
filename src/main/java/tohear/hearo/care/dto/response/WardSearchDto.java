package tohear.hearo.care.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.auth.domain.UserType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WardSearchDto {

    private String wardUserId;
    private String wardUserName;
    private UserType userType;

}
