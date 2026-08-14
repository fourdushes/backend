package tohear.hearo.institution.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.institution.InstitutionUserState;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeUserDto {

    private String userId;
    private String username;
    private String userEmail;
    private InstitutionUserState state;
    private String institutionName;

}
