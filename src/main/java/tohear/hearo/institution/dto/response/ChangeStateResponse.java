package tohear.hearo.institution.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.institution.InstitutionUserState;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStateResponse {

    private String institutionsUserId;
    private InstitutionUserState institutionState;

}
