package tohear.hearo.medicaltreatment.record.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompleteRecordRequest {

    private MultipartFile file;
    private Long archiveId;
    private String WardUserId;
}
