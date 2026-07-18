package tohear.hearo.user.auth.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailCheckNumberDto {

    private String email;
    private String checkNumber;

}
