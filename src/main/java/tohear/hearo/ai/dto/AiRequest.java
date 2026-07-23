package tohear.hearo.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiRequest {

    private String wardUserId; // 피보호자 이름
    private Long archiveId; // 아카이브 이름
    private String allChatText; // 채팅 그대로 보내주기

}
