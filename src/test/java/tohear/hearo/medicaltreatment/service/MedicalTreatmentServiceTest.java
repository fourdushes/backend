package tohear.hearo.medicaltreatment.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import tohear.hearo.archive.domain.Archive;
import tohear.hearo.archive.repository.ArchiveRepository;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;
import tohear.hearo.medicaltreatment.chat.domain.ChatMessage;
import tohear.hearo.medicaltreatment.chat.domain.ChatMessageType;
import tohear.hearo.medicaltreatment.chat.domain.ChatRoom;
import tohear.hearo.medicaltreatment.chat.domain.ChatRoomStatus;
import tohear.hearo.medicaltreatment.chat.domain.MessageSenderType;
import tohear.hearo.medicaltreatment.chat.repository.ChatMessageRepository;
import tohear.hearo.medicaltreatment.chat.repository.ChatRoomRepository;
import tohear.hearo.medicaltreatment.institution.repository.InstitutionSearchRepository;
import tohear.hearo.medicaltreatment.medicalrequest.domain.MedicalRequest;
import tohear.hearo.medicaltreatment.medicalrequest.domain.MedicalRequestStatus;
import tohear.hearo.medicaltreatment.medicalrequest.repository.MedicalRequestRepository;
import tohear.hearo.medicaltreatment.record.domain.Record;
import tohear.hearo.medicaltreatment.record.dto.response.CompleteRecordResponse;
import tohear.hearo.medicaltreatment.record.service.RecordService;
import tohear.hearo.medicaltreatment.record.repository.MedicalRecordLookupRepository;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.institution.InstitutionsUser;
import tohear.hearo.user.ward.WardUser;
import tohear.hearo.user.ward.WardUserRepository;

@ExtendWith(MockitoExtension.class)
class MedicalTreatmentServiceTest {

    @Mock InstitutionSearchRepository institutionRepository;
    @Mock WardUserRepository wardUserRepository;
    @Mock MedicalRequestRepository medicalRequestRepository;
    @Mock ArchiveRepository archiveRepository;
    @Mock ChatRoomRepository chatRoomRepository;
    @Mock ChatMessageRepository chatMessageRepository;
    @Mock RecordService recordService;
    @Mock MedicalRecordLookupRepository recordLookupRepository;

    private MedicalTreatmentService service;

    @BeforeEach
    void setUp() {
        service = new MedicalTreatmentService(
                institutionRepository, wardUserRepository, medicalRequestRepository, archiveRepository,
                chatRoomRepository, chatMessageRepository, recordService, recordLookupRepository);
    }

    @Test
    void wardCreatesRequestButDuplicateActiveRequestIsRejected() {
        when(wardUserRepository.findById("ward")).thenReturn(Optional.of(ward()));
        when(institutionRepository.findById("doctor")).thenReturn(Optional.of(doctor()));
        when(medicalRequestRepository.save(any(MedicalRequest.class))).thenAnswer(invocation -> {
            MedicalRequest request = invocation.getArgument(0);
            ReflectionTestUtils.setField(request, "id", 1L);
            return request;
        });

        assertThat(service.createRequest(wardPrincipal(), "doctor").getMedicalRequestId()).isEqualTo(1L);

        when(medicalRequestRepository.existsByWardUserIdAndInstitutionUserIdAndStatusIn(
                any(), any(), any())).thenReturn(true);
        assertThatThrownBy(() -> service.createRequest(wardPrincipal(), "doctor"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void onlyTargetInstitutionCanAcceptRequest() {
        MedicalRequest request = request();
        when(medicalRequestRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.acceptRequest(
                new MedicalUserPrincipal("other", UserType.INSTITUTIONS), 1L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(request.getStatus()).isEqualTo(MedicalRequestStatus.REQUESTED);

        assertThat(service.acceptRequest(institutionPrincipal(), 1L).getStatus())
                .isEqualTo(MedicalRequestStatus.ACCEPTED);
    }

    @Test
    void acceptedRequestStartsWithArchiveRoomAndFirstMessage() {
        MedicalRequest request = request();
        request.accept();
        when(medicalRequestRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(request));
        when(archiveRepository.saveAndFlush(any(Archive.class))).thenAnswer(invocation -> {
            Archive archive = invocation.getArgument(0);
            ReflectionTestUtils.setField(archive, "id", 10L);
            return archive;
        });
        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> {
            ChatRoom room = invocation.getArgument(0);
            ReflectionTestUtils.setField(room, "id", 20L);
            return room;
        });

        var response = service.startTreatment(wardPrincipal(), 1L);

        assertThat(response.getArchiveId()).isEqualTo(10L);
        assertThat(response.getChatRoomId()).isEqualTo(20L);
        assertThat(request.getStatus()).isEqualTo(MedicalRequestStatus.IN_PROGRESS);
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    void rejectedRequestCannotStartAndDifferentWardCannotStart() {
        MedicalRequest rejected = request();
        rejected.reject();
        when(medicalRequestRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(rejected));
        assertThatThrownBy(() -> service.startTreatment(wardPrincipal(), 1L))
                .isInstanceOf(IllegalStateException.class);

        MedicalRequest accepted = request();
        accepted.accept();
        when(medicalRequestRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(accepted));
        assertThatThrownBy(() -> service.startTreatment(
                new MedicalUserPrincipal("other", UserType.WARD), 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void wardSendsTextButBlankAndCompletedRoomAreRejected() {
        ChatRoom room = room();
        when(chatRoomRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(room));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(service.sendWardMessage(wardPrincipal(), 20L, "목이 아파요").getContent()).isEqualTo("목이 아파요");
        assertThatThrownBy(() -> service.sendWardMessage(wardPrincipal(), 20L, " "))
                .isInstanceOf(IllegalArgumentException.class);

        room.complete();
        assertThatThrownBy(() -> service.sendWardMessage(wardPrincipal(), 20L, "다시 전송"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void nonParticipantCannotReadMessages() {
        ChatRoom room = room();
        when(chatRoomRepository.findDetailById(20L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> service.getMessages(
                new MedicalUserPrincipal("stranger", UserType.WARD), 20L))
                .isInstanceOf(IllegalArgumentException.class);
        verify(chatMessageRepository, never()).findAllByChatRoomIdOrderByCreatedAtAscIdAsc(20L);
    }

    @Test
    void institutionCompletesRecordingAndSavesVoiceTranscriptWithRecord() throws Exception {
        ChatRoom room = room();
        MockMultipartFile file = new MockMultipartFile("file", "voice.webm", "audio/webm", new byte[] {1});
        Record record = new Record("voice.webm", LocalDateTime.now(), room.getArchive(), room.getWardUser());
        ReflectionTestUtils.setField(record, "id", 30L);
        when(chatRoomRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(room));
        when(recordService.completeRecord(any())).thenReturn(new CompleteRecordResponse("열은 없으신가요?", "now"));
        when(recordLookupRepository.findFirstByArchiveIdAndRecordFileOrderByRecordDateDescIdDesc(
                10L, "https://my-bucket.s3.ap-northeast-2.amazonaws.com/audio/voice.webm"))
                .thenReturn(Optional.of(record));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.completeRecording(institutionPrincipal(), 20L, file);

        assertThat(response.getMessageType()).isEqualTo(ChatMessageType.VOICE_TRANSCRIPT);
        assertThat(response.getContent()).isEqualTo("열은 없으신가요?");
        assertThat(response.getRecordId()).isEqualTo(30L);
        verify(recordService).completeRecord(any());
    }

    @Test
    void recordingIsRejectedForWardAndEmptyTranscriptCreatesNoMessage() throws Exception {
        ChatRoom room = room();
        MockMultipartFile file = new MockMultipartFile("file", "voice.webm", "audio/webm", new byte[] {1});
        when(chatRoomRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> service.completeRecording(wardPrincipal(), 20L, file))
                .isInstanceOf(IllegalArgumentException.class);

        when(recordService.completeRecord(any())).thenReturn(new CompleteRecordResponse(" ", "now"));
        assertThatThrownBy(() -> service.completeRecording(institutionPrincipal(), 20L, file))
                .isInstanceOf(IllegalStateException.class);
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }

    @Test
    void wardCompletesTreatmentAndArchiveStoresOrderedChatText() {
        ChatRoom room = room();
        ChatMessage first = message(room, MessageSenderType.INSTITUTION_USER, "doctor", "어디가 불편해서 오셨나요?");
        ChatMessage second = message(room, MessageSenderType.WARD_USER, "ward", "목이 아파요.");
        when(chatRoomRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(room));
        when(chatMessageRepository.findAllByChatRoomIdOrderByCreatedAtAscIdAsc(20L))
                .thenReturn(List.of(first, second));

        service.completeTreatment(wardPrincipal(), 20L);

        assertThat(room.getArchive().getAllChatText()).isEqualTo(
                "상대: 어디가 불편해서 오셨나요?" + System.lineSeparator() + "나: 목이 아파요.");
        assertThat(room.getStatus()).isEqualTo(ChatRoomStatus.COMPLETED);
        assertThat(room.getMedicalRequest().getStatus()).isEqualTo(MedicalRequestStatus.COMPLETED);
    }

    @Test
    void institutionCannotCompleteTreatment() {
        assertThatThrownBy(() -> service.completeTreatment(institutionPrincipal(), 20L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private MedicalRequest request() {
        MedicalRequest request = new MedicalRequest(ward(), doctor());
        ReflectionTestUtils.setField(request, "id", 1L);
        return request;
    }

    private ChatRoom room() {
        MedicalRequest request = request();
        request.accept();
        request.start();
        Archive archive = new Archive("", request.getWardUser());
        ReflectionTestUtils.setField(archive, "id", 10L);
        ChatRoom room = new ChatRoom(request, archive);
        ReflectionTestUtils.setField(room, "id", 20L);
        return room;
    }

    private ChatMessage message(ChatRoom room, MessageSenderType senderType, String senderId, String content) {
        return new ChatMessage(room, senderType, senderId, ChatMessageType.TEXT, content, null);
    }

    private WardUser ward() {
        return new WardUser("ward", "환자", "ward@test.com", "pw", UserType.WARD);
    }

    private InstitutionsUser doctor() {
        return new InstitutionsUser("doctor", "의사", "doctor@test.com", "pw", UserType.INSTITUTIONS);
    }

    private MedicalUserPrincipal wardPrincipal() {
        return new MedicalUserPrincipal("ward", UserType.WARD);
    }

    private MedicalUserPrincipal institutionPrincipal() {
        return new MedicalUserPrincipal("doctor", UserType.INSTITUTIONS);
    }
}
