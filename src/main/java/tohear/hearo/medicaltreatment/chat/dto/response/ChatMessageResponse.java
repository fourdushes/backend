package tohear.hearo.medicaltreatment.chat.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.medicaltreatment.chat.domain.ChatMessageType;
import tohear.hearo.medicaltreatment.chat.domain.MessageSenderType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private Long messageId;
    private Long chatRoomId;
    private MessageSenderType senderType;
    private String senderId;
    private String senderName;
    private ChatMessageType messageType;
    private String content;
    private Long recordId;
    private LocalDateTime createdAt;
    private boolean isMine;
}
