package tohear.hearo.medicaltreatment.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import tohear.hearo.ai.dto.AiRequest;
import tohear.hearo.ai.dto.AiResponse;
import tohear.hearo.ai.service.AiService;
import tohear.hearo.archive.domain.Archive;
import tohear.hearo.archive.repository.ArchiveRepository;
import tohear.hearo.medicaltreatment.chat.domain.ChatMessage;
import tohear.hearo.medicaltreatment.chat.domain.ChatMessageType;
import tohear.hearo.medicaltreatment.chat.domain.ChatRoom;
import tohear.hearo.medicaltreatment.chat.domain.MessageSenderType;
import tohear.hearo.medicaltreatment.chat.dto.response.ChatMessageResponse;
import tohear.hearo.medicaltreatment.chat.dto.response.ChatRoomResponse;
import tohear.hearo.medicaltreatment.chat.dto.response.ChatRoomResponse.WardSummary;
import tohear.hearo.medicaltreatment.chat.dto.response.StartMedicalTreatmentResponse;
import tohear.hearo.medicaltreatment.chat.repository.ChatMessageRepository;
import tohear.hearo.medicaltreatment.chat.repository.ChatRoomRepository;
import tohear.hearo.medicaltreatment.institution.dto.response.InstitutionResponse;
import tohear.hearo.medicaltreatment.institution.repository.InstitutionSearchRepository;
import tohear.hearo.medicaltreatment.medicalrequest.domain.MedicalRequest;
import tohear.hearo.medicaltreatment.medicalrequest.domain.MedicalRequestStatus;
import tohear.hearo.medicaltreatment.medicalrequest.dto.response.MedicalRequestResponse;
import tohear.hearo.medicaltreatment.medicalrequest.repository.MedicalRequestRepository;
import tohear.hearo.medicaltreatment.record.dto.CompletedRecord;
import tohear.hearo.medicaltreatment.record.dto.request.CompleteRecordRequest;
import tohear.hearo.medicaltreatment.record.service.RecordService;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;
import tohear.hearo.user.institution.InstitutionsUser;
import tohear.hearo.user.ward.WardUser;
import tohear.hearo.user.ward.WardUserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalTreatmentService {

    private static final String FIRST_MESSAGE = "어디가 불편해서 오셨나요?";
    private static final Set<MedicalRequestStatus> ACTIVE_REQUEST_STATUSES = Set.of(
            MedicalRequestStatus.REQUESTED,
            MedicalRequestStatus.ACCEPTED,
            MedicalRequestStatus.IN_PROGRESS);

    private final InstitutionSearchRepository institutionRepository;
    private final WardUserRepository wardUserRepository;
    private final MedicalRequestRepository medicalRequestRepository;
    private final ArchiveRepository archiveRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RecordService recordService;
    private final AiService aiService;

    public List<InstitutionResponse> searchInstitutions(MedicalUserPrincipal principal, String keyword) {
        requireType(principal, UserType.WARD);
        return institutionRepository.search(keyword).stream().map(this::toInstitutionResponse).toList();
    }

    @Transactional
    public MedicalRequestResponse createRequest(MedicalUserPrincipal principal, String institutionUserId) {
        requireType(principal, UserType.WARD);
        WardUser wardUser = findWard(principal.getUserId());
        InstitutionsUser institutionUser = findInstitution(institutionUserId);
        if (medicalRequestRepository.existsByWardUserIdAndInstitutionUserIdAndStatusIn(
                wardUser.getId(), institutionUser.getId(), List.copyOf(ACTIVE_REQUEST_STATUSES))) {
            throw new IllegalStateException("이미 처리 중인 진료 요청이 있습니다.");
        }

        return toMedicalRequestResponse(medicalRequestRepository.save(new MedicalRequest(wardUser, institutionUser)));
    }

    public List<MedicalRequestResponse> getSentRequests(MedicalUserPrincipal principal) {
        requireType(principal, UserType.WARD);
        return medicalRequestRepository.findAllByWardUserIdOrderByCreatedAtDesc(principal.getUserId())
                .stream().map(this::toMedicalRequestResponse).toList();
    }

    public List<MedicalRequestResponse> getReceivedRequests(MedicalUserPrincipal principal) {
        requireType(principal, UserType.INSTITUTIONS);
        return medicalRequestRepository.findAllByInstitutionUserIdOrderByCreatedAtDesc(principal.getUserId())
                .stream().map(this::toMedicalRequestResponse).toList();
    }

    public MedicalRequestResponse getRequest(MedicalUserPrincipal principal, Long requestId) {
        MedicalRequest request = medicalRequestRepository.findDetailById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("진료 요청을 찾을 수 없습니다."));
        validateRequestParticipant(request, principal);
        return toMedicalRequestResponse(request);
    }

    @Transactional
    public MedicalRequestResponse acceptRequest(MedicalUserPrincipal principal, Long requestId) {
        MedicalRequest request = findRequestForUpdate(requestId);
        requireInstitutionOwner(request, principal);
        request.accept();
        return toMedicalRequestResponse(request);
    }

    @Transactional
    public MedicalRequestResponse rejectRequest(MedicalUserPrincipal principal, Long requestId) {
        MedicalRequest request = findRequestForUpdate(requestId);
        requireInstitutionOwner(request, principal);
        request.reject();
        return toMedicalRequestResponse(request);
    }

    @Transactional
    public StartMedicalTreatmentResponse startTreatment(MedicalUserPrincipal principal, Long requestId) {
        MedicalRequest request = findRequestForUpdate(requestId);
        requireType(principal, UserType.WARD);
        if (!request.getWardUser().getId().equals(principal.getUserId())) {
            throw new IllegalArgumentException("요청을 보낸 피보호자만 진료를 시작할 수 있습니다.");
        }
        if (chatRoomRepository.existsByMedicalRequestId(requestId)) {
            throw new IllegalStateException("이미 시작된 진료입니다.");
        }

        request.start();
        Archive archive = archiveRepository.saveAndFlush(new Archive(
                "",
                request.getWardUser()));
        ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(request, archive));
        chatMessageRepository.save(new ChatMessage(
                chatRoom,
                MessageSenderType.INSTITUTION_USER,
                request.getInstitutionUser().getId(),
                ChatMessageType.TEXT,
                FIRST_MESSAGE,
                null));
        return new StartMedicalTreatmentResponse(chatRoom.getId(), archive.getId());
    }

    public ChatRoomResponse getChatRoom(MedicalUserPrincipal principal, Long chatRoomId) {
        ChatRoom chatRoom = findChatRoom(chatRoomId);
        validateChatParticipant(chatRoom, principal);
        return toChatRoomResponse(chatRoom);
    }

    public List<ChatMessageResponse> getMessages(MedicalUserPrincipal principal, Long chatRoomId) {
        ChatRoom chatRoom = findChatRoom(chatRoomId);
        validateChatParticipant(chatRoom, principal);
        return chatMessageRepository.findAllByChatRoomIdOrderByCreatedAtAscIdAsc(chatRoomId)
                .stream().map(message -> toMessageResponse(message, chatRoom, principal)).toList();
    }

    @Transactional
    public ChatMessageResponse sendWardMessage(MedicalUserPrincipal principal, Long chatRoomId, String content) {
        requireType(principal, UserType.WARD);
        ChatRoom chatRoom = findChatRoomForUpdate(chatRoomId);
        if (!chatRoom.getWardUser().getId().equals(principal.getUserId())) {
            throw new IllegalArgumentException("해당 채팅방의 피보호자가 아닙니다.");
        }
        chatRoom.validateInProgress();
        ChatMessage message = chatMessageRepository.save(new ChatMessage(
                chatRoom, MessageSenderType.WARD_USER, principal.getUserId(), ChatMessageType.TEXT, content, null));
        return toMessageResponse(message, chatRoom, principal);
    }

    @Transactional
    public ChatMessageResponse completeRecording(MedicalUserPrincipal principal, Long chatRoomId,
                                                 MultipartFile file) throws IOException {
        requireType(principal, UserType.INSTITUTIONS);
        ChatRoom chatRoom = findChatRoomForUpdate(chatRoomId);
        if (!chatRoom.getInstitutionUser().getId().equals(principal.getUserId())) {
            throw new IllegalArgumentException("해당 채팅방의 기관 사용자만 녹음을 종료할 수 있습니다.");
        }
        chatRoom.validateInProgress();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("녹음 파일이 필요합니다.");
        }

        CompleteRecordRequest recordRequest = new CompleteRecordRequest();
        recordRequest.setFile(file);
        recordRequest.setArchiveId(chatRoom.getArchive().getId());
        recordRequest.setWardUserId(chatRoom.getWardUser().getId());
        CompletedRecord completedRecord = recordService.completeRecord(recordRequest);
        String recordText = completedRecord.recordText();
        if (recordText == null || recordText.isBlank()) {
            throw new IllegalStateException("음성 변환 결과가 비어 있습니다.");
        }

        ChatMessage message = chatMessageRepository.save(new ChatMessage(
                chatRoom,
                MessageSenderType.INSTITUTION_USER,
                principal.getUserId(),
                ChatMessageType.VOICE_TRANSCRIPT,
                recordText,
                completedRecord.record()));
        return toMessageResponse(message, chatRoom, principal);
    }

    @Transactional
    public ChatRoomResponse completeTreatment(MedicalUserPrincipal principal, Long chatRoomId) {
        requireType(principal, UserType.WARD);
        ChatRoom chatRoom = findChatRoomForUpdate(chatRoomId);
        if (!chatRoom.getWardUser().getId().equals(principal.getUserId())) {
            throw new IllegalArgumentException("해당 진료의 피보호자만 진료를 종료할 수 있습니다.");
        }
        chatRoom.validateInProgress();

        String allChatText = chatMessageRepository.findAllByChatRoomIdOrderByCreatedAtAscIdAsc(chatRoomId)
                .stream()
                .filter(message -> message.getSenderType() != MessageSenderType.SYSTEM)
                .filter(message -> message.getContent() != null && !message.getContent().isBlank())
                .map(message -> (message.getSenderType() == MessageSenderType.WARD_USER ? "나: " : "기관: ")
                        + message.getContent().trim())
                .reduce((left, right) -> left + System.lineSeparator() + right)
                .orElse("");

        AiResponse response = aiService.getSummary(
            new AiRequest(chatRoom.getWardUser().getId(),
                          chatRoom.getArchive().getId(),
                          allChatText));

        chatRoom.getArchive().updateText(response.getSummary());


        chatRoom.getArchive().updateAllChatText(allChatText);
        chatRoom.getMedicalRequest().complete();
        chatRoom.complete();
        return toChatRoomResponse(chatRoom);
    }

    private MedicalRequest findRequestForUpdate(Long requestId) {
        return medicalRequestRepository.findByIdForUpdate(requestId)
                .orElseThrow(() -> new IllegalArgumentException("진료 요청을 찾을 수 없습니다."));
    }

    private ChatRoom findChatRoom(Long chatRoomId) {
        return chatRoomRepository.findDetailById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
    }

    private ChatRoom findChatRoomForUpdate(Long chatRoomId) {
        return chatRoomRepository.findByIdForUpdate(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
    }

    private WardUser findWard(String id) {
        return wardUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("피보호자를 찾을 수 없습니다."));
    }

    private InstitutionsUser findInstitution(String id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기관 사용자를 찾을 수 없습니다."));
    }

    private void requireInstitutionOwner(MedicalRequest request, MedicalUserPrincipal principal) {
        requireType(principal, UserType.INSTITUTIONS);
        if (!request.getInstitutionUser().getId().equals(principal.getUserId())) {
            throw new IllegalArgumentException("요청 대상 기관 사용자만 처리할 수 있습니다.");
        }
    }

    private void validateRequestParticipant(MedicalRequest request, MedicalUserPrincipal principal) {
        boolean ward = principal.getUserType() == UserType.WARD
                && request.getWardUser().getId().equals(principal.getUserId());
        boolean institution = principal.getUserType() == UserType.INSTITUTIONS
                && request.getInstitutionUser().getId().equals(principal.getUserId());
        if (!ward && !institution) {
            throw new IllegalArgumentException("해당 진료 요청을 조회할 권한이 없습니다.");
        }
    }

    private void validateChatParticipant(ChatRoom room, MedicalUserPrincipal principal) {
        boolean ward = principal.getUserType() == UserType.WARD && room.getWardUser().getId().equals(principal.getUserId());
        boolean institution = principal.getUserType() == UserType.INSTITUTIONS
                && room.getInstitutionUser().getId().equals(principal.getUserId());
        if (!ward && !institution) {
            throw new IllegalArgumentException("채팅방 참여자가 아닙니다.");
        }
    }

    private void requireType(MedicalUserPrincipal principal, UserType expected) {
        if (principal.getUserType() != expected) {
            throw new IllegalArgumentException("해당 사용자 유형은 이 기능을 사용할 수 없습니다.");
        }
    }

    private InstitutionResponse toInstitutionResponse(InstitutionsUser user) {
        return new InstitutionResponse(user.getId(), user.getName(), user.getEmail());
    }

    private MedicalRequestResponse toMedicalRequestResponse(MedicalRequest request) {
        return new MedicalRequestResponse(
                request.getId(), request.getWardUser().getId(), request.getWardUser().getName(),
                request.getInstitutionUser().getId(), request.getInstitutionUser().getName(), request.getStatus(),
                request.getCreatedAt(), request.getRespondedAt(), request.getStartedAt(), request.getCompletedAt());
    }

    private ChatMessageResponse toMessageResponse(ChatMessage message, ChatRoom room,
                                                  MedicalUserPrincipal principal) {
        String senderName = switch (message.getSenderType()) {
            case INSTITUTION_USER -> room.getInstitutionUser().getName();
            case WARD_USER -> room.getWardUser().getName();
            case SYSTEM -> "SYSTEM";
        };
        return new ChatMessageResponse(
                message.getId(), room.getId(), message.getSenderType(), message.getSenderId(), senderName,
                message.getMessageType(), message.getContent(),
                message.getRecord() == null ? null : message.getRecord().getId(), message.getCreatedAt(),
                message.getSenderId().equals(principal.getUserId()));
    }

    private ChatRoomResponse toChatRoomResponse(ChatRoom room) {
        ChatMessage lastMessage = chatMessageRepository
                .findFirstByChatRoomIdOrderByCreatedAtDescIdDesc(room.getId()).orElse(null);
        return new ChatRoomResponse(
                room.getId(), room.getMedicalRequest().getId(), room.getArchive().getId(),
                toInstitutionResponse(room.getInstitutionUser()),
                new WardSummary(room.getWardUser().getId(), room.getWardUser().getName()),
                room.getStatus(), room.getStartedAt(), room.getCompletedAt(),
                lastMessage == null ? null : lastMessage.getContent(),
                lastMessage == null ? null : lastMessage.getCreatedAt());
    }
}
