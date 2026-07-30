package tohear.hearo.institution.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InstitutionChangePasswordRequest {

    @NotNull(message = "기관 아이디는 필수입니다.")
    private Long institutionId;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String checkNewPassword;

    @NotBlank(message = "임시 토큰은 필수입니다.")
    private String tempToken;
}
