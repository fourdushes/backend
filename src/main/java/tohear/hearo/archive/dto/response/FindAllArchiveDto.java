package tohear.hearo.archive.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindAllArchiveDto {

    private long archiveId;
    private String arhciveName;
    private LocalDateTime archiveDate;
    

}
