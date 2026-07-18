package tohear.hearo.medicaltreatment.chat.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import tohear.hearo.archive.domain.Archive;
import tohear.hearo.medicaltreatment.medicalrequest.domain.MedicalRequest;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.institution.InstitutionsUser;
import tohear.hearo.user.ward.WardUser;

class ChatMessageTest {

    @Test
    void trimsMessageAndRejectsBlankContent() {
        ChatRoom room = room();
        ChatMessage message = new ChatMessage(
                room, MessageSenderType.WARD_USER, "ward", ChatMessageType.TEXT, "  아파요.  ", null);

        assertThat(message.getContent()).isEqualTo("아파요.");
        assertThatThrownBy(() -> new ChatMessage(
                room, MessageSenderType.WARD_USER, "ward", ChatMessageType.TEXT, "  ", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void completedRoomRejectsFurtherWork() {
        ChatRoom room = room();
        room.complete();

        assertThatThrownBy(room::validateInProgress).isInstanceOf(IllegalStateException.class);
    }

    private ChatRoom room() {
        WardUser ward = new WardUser("ward", "환자", "ward@test.com", "pw", UserType.WARD);
        InstitutionsUser doctor = new InstitutionsUser(
                "doctor", "의사", "doctor@test.com", "pw", UserType.INSTITUTIONS);
        MedicalRequest request = new MedicalRequest(ward, doctor);
        request.accept();
        request.start();
        return new ChatRoom(request, new Archive("", ward));
    }
}
