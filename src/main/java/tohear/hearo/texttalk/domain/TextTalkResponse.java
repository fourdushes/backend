package tohear.hearo.texttalk.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextTalkResponse {

    private String wardUserId; // 피보호자 이름
    private String wardUserName; // 피보호자 이름
    private String wardUserBirthDate; // 피보호자 생년월일
}
