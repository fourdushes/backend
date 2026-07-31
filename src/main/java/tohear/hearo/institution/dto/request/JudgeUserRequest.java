package tohear.hearo.institution.dto.request;

import org.springframework.data.domain.Pageable;

import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.institution.domain.InstitutionState;

@Data
@NoArgsConstructor
public class JudgeUserRequest {

    Long institutionId;
    InstitutionState state;
    Pageable pageable;
}
