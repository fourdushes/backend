package tohear.hearo.care.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FindWardToCareRequest {

    private String wardUserId; // 검색어

}
