package tohear.hearo.user.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginUserRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    private String id;
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

}
