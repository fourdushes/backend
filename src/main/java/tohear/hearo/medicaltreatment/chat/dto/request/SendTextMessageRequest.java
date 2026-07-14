package tohear.hearo.medicaltreatment.chat.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendTextMessageRequest {

    private String content;
}
