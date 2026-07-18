package tohear.hearo.user.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenReissueResponse {

    private String accessToken;
    private String refreshToken;
}
