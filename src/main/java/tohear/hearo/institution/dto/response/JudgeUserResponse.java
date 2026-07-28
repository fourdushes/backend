package tohear.hearo.institution.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeUserResponse {

    private Long count;
    private List<JudgeUserDto> judgeUserList;

}
