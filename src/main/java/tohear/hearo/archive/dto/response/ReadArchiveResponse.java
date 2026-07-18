package tohear.hearo.archive.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadArchiveResponse {

    private Long archiveId;
    private String title;
    private LocalDateTime archiveDate;
    private String text;
    private String allChatText;

}
