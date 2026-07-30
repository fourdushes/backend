package tohear.hearo.institution.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionToChangePasswordResponse {

    private Long institutionId;
    private String tempToken;
}
