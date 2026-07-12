package tohear.hearo.care.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckCareListResponse {

    private int totalCount; // 보호자 또는 피보호자의 연결 상태 리스트 총 개수
    private List<CheckCareListDto> careList; // 보호자 또는 피보호자의 연결 상태 리스트

}
