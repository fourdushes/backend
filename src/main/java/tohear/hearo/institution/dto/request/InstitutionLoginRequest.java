package tohear.hearo.institution.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InstitutionLoginRequest {

    private String loginId;
    private String password;

}
