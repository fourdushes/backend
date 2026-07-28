package tohear.hearo.institution.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.institution.domain.InstitutionState;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeUserDto {

    private String userId;
    private String username;
    private String userEmail;
    private LocalDateTime localDateTime;
    private InstitutionState state;

}
