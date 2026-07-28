package tohear.hearo.user.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.institution.domain.Institution;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionSearchResponse {

    private Long institutionId;
    private String institutionName;

    public static InstitutionSearchResponse from(Institution institution) {
        return new InstitutionSearchResponse(
            institution.getId(),
            institution.getInstitutionName()
        );
    }
}
