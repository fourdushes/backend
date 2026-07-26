package tohear.hearo.user.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.auth.domain.UserType;

@Data
@NoArgsConstructor
public class JoinUserRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 5, message = "아이디는 5자 이상이어야 합니다.")
    private String id;
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
    @NotNull(message = "사용자 유형은 필수입니다.")
    private UserType userType; // 사용자 유형 (피보호자, 보호자)

}
