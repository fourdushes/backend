package tohear.hearo.user.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenReissueRequest {

    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
}
