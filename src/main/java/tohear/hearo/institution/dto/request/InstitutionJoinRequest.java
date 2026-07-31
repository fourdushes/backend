package tohear.hearo.institution.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InstitutionJoinRequest {

    private String institutionName;
    private String email;
    private String institutionId;
    private String password;
    private String checkPassword;

}
