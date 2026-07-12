package tohear.hearo.care.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindWardToCareResponse {

    private int totalCount; // 검색이 완료된 피보호자의 총 개수
    private List<FindWardToCareDto> wardUserList; // 검색이 완료된 피보호자 리스트

}
