package tohear.hearo.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiResponse {

    private String wardUserId; // 피보호자 이름
    private Long archiveId; // 아카이브 이름
    private String allChatText; // 채팅 그대로 받아오기
    private String mainSymptoms;
    private String doctorOpinion;
    private String remember;
    private String questionAnswer;
    private String difficultWords;

    // AI 서버 응답 예: "disease": "COLD" 또는 "감기". 분류 시 누락/미등록 값은 OTHER로 처리한다.
    private String disease;

}
