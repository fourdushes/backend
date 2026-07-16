package tohear.hearo.user.auth.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenReissueRequest {

    private String refreshToken;
}
