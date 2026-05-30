package tohear.hearo.global.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailCheckNumberDto {

    private String email;
    private String checkNumber;

}
