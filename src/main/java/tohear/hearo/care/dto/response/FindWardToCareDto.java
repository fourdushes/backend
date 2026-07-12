package tohear.hearo.care.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindWardToCareDto {

    private String wardUserId; // 검색이 완료된 피보호자 아이디
    private String wardUserName; // 검색이 완료된 피보호자 이름

}
