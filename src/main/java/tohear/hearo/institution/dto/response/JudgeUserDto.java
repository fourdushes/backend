package tohear.hearo.institution.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.institution.InstitutionState;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeUserDto {

    private String userId;
    private String username;
    private String userEmail;
    private InstitutionState state;
    private String institutionName;

}
