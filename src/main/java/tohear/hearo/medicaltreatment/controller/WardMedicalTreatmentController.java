package tohear.hearo.medicaltreatment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.response.Result;
import tohear.hearo.user.auth.principal.CurrentMedicalUser;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;
import tohear.hearo.medicaltreatment.chat.dto.request.SendTextMessageRequest;
import tohear.hearo.medicaltreatment.chat.dto.response.ChatMessageResponse;
import tohear.hearo.medicaltreatment.chat.dto.response.ChatRoomResponse;
import tohear.hearo.medicaltreatment.chat.dto.response.StartMedicalTreatmentResponse;
import tohear.hearo.medicaltreatment.institution.dto.response.InstitutionResponse;
import tohear.hearo.medicaltreatment.medicalrequest.dto.request.CreateMedicalRequest;
import tohear.hearo.medicaltreatment.medicalrequest.dto.response.MedicalRequestResponse;
import tohear.hearo.medicaltreatment.service.MedicalTreatmentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medical-treatment/ward")
public class WardMedicalTreatmentController {

    private final MedicalTreatmentService medicalTreatmentService;

    // 피보호자가 진료 요청을 보낼 기관 사용자를 검색합니다.
    @GetMapping("/institutions")
    public Result<List<InstitutionResponse>> searchInstitutions(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @RequestParam(required = false) String keyword) {
        return new Result<>("200", "기관 사용자 검색에 성공했습니다.",
                medicalTreatmentService.searchInstitutions(principal, keyword));
    }

    // 검색한 기관 사용자에게 진료를 요청합니다.
    @PostMapping("/requests")
    public Result<MedicalRequestResponse> createRequest(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @RequestBody CreateMedicalRequest request) {
        return new Result<>("200", "진료 요청을 전송했습니다.",
                medicalTreatmentService.createRequest(principal, request.getInstitutionUserId()));
    }

    // 피보호자가 자신이 보낸 진료 요청 목록을 조회합니다.
    @GetMapping("/requests")
    public Result<List<MedicalRequestResponse>> getRequests(
            @CurrentMedicalUser MedicalUserPrincipal principal) {
        return new Result<>("200", "보낸 진료 요청 목록 조회에 성공했습니다.",
                medicalTreatmentService.getSentRequests(principal));
    }

    // 피보호자가 자신이 보낸 진료 요청의 상태를 조회합니다.
    @GetMapping("/requests/{requestId}")
    public Result<MedicalRequestResponse> getRequest(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long requestId) {
        return new Result<>("200", "진료 요청 조회에 성공했습니다.",
                medicalTreatmentService.getRequest(principal, requestId));
    }

    // 기관 사용자가 수락한 요청으로 진료와 채팅방을 시작합니다.
    @PostMapping("/requests/{requestId}/start")
    public Result<StartMedicalTreatmentResponse> startTreatment(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long requestId) {
        return new Result<>("200", "진료를 시작했습니다.",
                medicalTreatmentService.startTreatment(principal, requestId));
    }

    // 피보호자가 자신이 참여한 채팅방 정보를 조회합니다.
    @GetMapping("/chat-rooms/{chatRoomId}")
    public Result<ChatRoomResponse> getChatRoom(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long chatRoomId) {
        return new Result<>("200", "채팅방 조회에 성공했습니다.",
                medicalTreatmentService.getChatRoom(principal, chatRoomId));
    }

    // 피보호자가 채팅방 메시지를 시간순으로 조회합니다.
    @GetMapping("/chat-rooms/{chatRoomId}/messages")
    public Result<List<ChatMessageResponse>> getMessages(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long chatRoomId) {
        return new Result<>("200", "채팅 메시지 조회에 성공했습니다.",
                medicalTreatmentService.getMessages(principal, chatRoomId));
    }

    // 피보호자가 기관 사용자에게 텍스트 메시지를 전송합니다.
    @PostMapping("/chat-rooms/{chatRoomId}/messages")
    public Result<ChatMessageResponse> sendMessage(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long chatRoomId,
            @RequestBody SendTextMessageRequest request) {
        return new Result<>("200", "채팅 메시지를 전송했습니다.",
                medicalTreatmentService.sendWardMessage(principal, chatRoomId, request.getContent()));
    }

    // 피보호자가 진료를 종료하고 전체 채팅 내용을 Archive에 저장합니다.
    @PostMapping("/chat-rooms/{chatRoomId}/complete")
    public Result<ChatRoomResponse> completeTreatment(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long chatRoomId) {
        return new Result<>("200", "진료를 종료했습니다.",
                medicalTreatmentService.completeTreatment(principal, chatRoomId));
    }
}
