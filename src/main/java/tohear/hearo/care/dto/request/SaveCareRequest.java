package tohear.hearo.care.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaveCareRequest {

    private String guardUserId;
    private String wardUserId;
}
