package tohear.hearo.care.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.care.domain.CareState;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckCareListDto {

    private String wardUserId; // 피보호자 아이디
    private String guardUserId; // 보호자 아이디
    private CareState careState; // 연결 상태 (승인, 대기, 거절)
    private LocalDateTime createdAt; // 연결 생성 시간
    private LocalDateTime updatedAt; // 연결 상태 변경 시간

}
