package tohear.hearo.global.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenReissueRequest {

    private String refreshToken;
}
