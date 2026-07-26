package tohear.hearo.medicaltreatment.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendTextMessageRequest {

    @NotBlank(message = "메시지 내용은 필수입니다.")
    private String content;
}
