package tohear.hearo.user.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.user.auth.domain.UserType;

@Data
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    private String id;
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;
    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String checkNewPassword;
    @NotNull(message = "사용자 유형은 필수입니다.")
    private UserType userType;
    @NotBlank(message = "임시 토큰은 필수입니다.")
    private String tempToken; // 임시 토큰 추가
    

}
