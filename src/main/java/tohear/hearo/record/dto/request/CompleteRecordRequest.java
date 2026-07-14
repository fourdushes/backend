package tohear.hearo.record.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompleteRecordRequest {

    private byte[] recordFile;
    private String WardUserId;
}
