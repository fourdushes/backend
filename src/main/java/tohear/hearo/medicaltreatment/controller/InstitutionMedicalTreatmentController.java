package tohear.hearo.medicaltreatment.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.Result;
import tohear.hearo.medicaltreatment.auth.CurrentMedicalUser;
import tohear.hearo.medicaltreatment.auth.MedicalUserPrincipal;
import tohear.hearo.medicaltreatment.chat.dto.response.ChatMessageResponse;
import tohear.hearo.medicaltreatment.chat.dto.response.ChatRoomResponse;
import tohear.hearo.medicaltreatment.medicalrequest.dto.response.MedicalRequestResponse;
import tohear.hearo.medicaltreatment.service.MedicalTreatmentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medical-treatment/institution")
public class InstitutionMedicalTreatmentController {

    private final MedicalTreatmentService medicalTreatmentService;

    // 기관 사용자가 자신에게 도착한 진료 요청 목록을 조회합니다.
    @GetMapping("/requests")
    public Result<List<MedicalRequestResponse>> getRequests(
            @CurrentMedicalUser MedicalUserPrincipal principal) {
        return new Result<>("200", "받은 진료 요청 목록 조회에 성공했습니다.",
                medicalTreatmentService.getReceivedRequests(principal));
    }

    // 기관 사용자가 자신에게 도착한 진료 요청의 상세 상태를 조회합니다.
    @GetMapping("/requests/{requestId}")
    public Result<MedicalRequestResponse> getRequest(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long requestId) {
        return new Result<>("200", "진료 요청 조회에 성공했습니다.",
                medicalTreatmentService.getRequest(principal, requestId));
    }

    // 기관 사용자가 자신에게 도착한 진료 요청을 수락합니다.
    @PostMapping("/requests/{requestId}/accept")
    public Result<MedicalRequestResponse> acceptRequest(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long requestId) {
        return new Result<>("200", "진료 요청을 수락했습니다.",
                medicalTreatmentService.acceptRequest(principal, requestId));
    }

    // 기관 사용자가 자신에게 도착한 진료 요청을 거절합니다.
    @PostMapping("/requests/{requestId}/reject")
    public Result<MedicalRequestResponse> rejectRequest(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long requestId) {
        return new Result<>("200", "진료 요청을 거절했습니다.",
                medicalTreatmentService.rejectRequest(principal, requestId));
    }

    // 기관 사용자가 자신이 참여한 채팅방 메시지를 시간순으로 조회합니다.
    @GetMapping("/chat-rooms/{chatRoomId}/messages")
    public Result<List<ChatMessageResponse>> getMessages(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long chatRoomId) {
        return new Result<>("200", "채팅 메시지 조회에 성공했습니다.",
                medicalTreatmentService.getMessages(principal, chatRoomId));
    }

    // 기관 사용자의 녹음 파일을 텍스트로 변환하고 채팅 메시지로 저장합니다.
    @PostMapping(value = "/chat-rooms/{chatRoomId}/recordings/complete",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ChatMessageResponse> completeRecording(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long chatRoomId,
            @RequestPart("file") MultipartFile file) throws IOException {
        return new Result<>("200", "녹음 종료 및 채팅 메시지 저장에 성공했습니다.",
                medicalTreatmentService.completeRecording(principal, chatRoomId, file));
    }

}
