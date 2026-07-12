package tohear.hearo.care.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WardSearchResponse {

    private int totalCount;
    private List<WardSearchDto> wardSearchList;
    



}
