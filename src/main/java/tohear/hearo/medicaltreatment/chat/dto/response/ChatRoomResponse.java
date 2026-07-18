package tohear.hearo.medicaltreatment.chat.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tohear.hearo.medicaltreatment.chat.domain.ChatRoomStatus;
import tohear.hearo.medicaltreatment.institution.dto.response.InstitutionResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {

    private Long chatRoomId;
    private Long medicalRequestId;
    private Long archiveId;
    private InstitutionResponse institutionUser;
    private WardSummary wardUser;
    private ChatRoomStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String lastMessage;
    private LocalDateTime lastMessageAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WardSummary {

        private String wardUserId;
        private String name;
    }
}
