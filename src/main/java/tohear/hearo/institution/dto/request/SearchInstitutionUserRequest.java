package tohear.hearo.institution.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.institution.InstitutionUserState;

@Data
@NoArgsConstructor
public class SearchInstitutionUserRequest {

    private String keyword;
    private InstitutionUserState institutionUserState;

}
