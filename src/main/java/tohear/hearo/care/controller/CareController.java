package tohear.hearo.care.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.care.dto.request.ChangeCareStateRequest;
import tohear.hearo.care.dto.request.CheckMainGuardRequest;
import tohear.hearo.care.dto.request.DeleteCardRequest;
import tohear.hearo.care.dto.request.FindWardToCareRequest;
import tohear.hearo.care.dto.request.SaveCareRequest;
import tohear.hearo.care.dto.response.ChangeCareStateResponse;
import tohear.hearo.care.dto.response.CheckCareListResponse;
import tohear.hearo.care.dto.response.CheckMainGuardResponse;
import tohear.hearo.care.dto.response.FindWardToCareResponse;
import tohear.hearo.care.dto.response.GuardSearchResponse;
import tohear.hearo.care.dto.response.SaveCareResponse;
import tohear.hearo.care.dto.response.WardSearchResponse;
import tohear.hearo.care.service.CareService;
import tohear.hearo.global.response.Result;
import tohear.hearo.user.auth.principal.CurrentMedicalUser;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;



@RestController
@RequiredArgsConstructor
public class CareController {

    private final CareService careService;

    // 보호자가 피보호자를 검색하는 API
    @GetMapping("/api/care/user/search-ward-user")
    public Result searchWardUserToCare(@CurrentMedicalUser MedicalUserPrincipal principal,
                                       @ModelAttribute FindWardToCareRequest request,
                                       @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        FindWardToCareResponse response = careService.findWardToCare(principal, request, pageable);
        return new Result<>("200", "피보호자 검색이 성공했습니다.", response);
    }

    // 보호자가 피보호자에게 연결을 신청하는 API
    @PostMapping("/api/care/user/save-care")
    public Result saveCare(@CurrentMedicalUser MedicalUserPrincipal principal, @RequestBody SaveCareRequest request) {
        SaveCareResponse response = careService.saveCare(principal, request);
        return new Result<>("200", "연결 저장에 성공했습니다.", response);
    }
    
    // 피보호자가 연결 신청한 리스트 확인
    @GetMapping("/api/care/user/ward/check-care-list")
    public Result checkWardCareList(@CurrentMedicalUser MedicalUserPrincipal principal) {
        CheckCareListResponse response = careService.checkCareListByWardUser(principal);
        return new Result<>("200", "연결 리스트 확인에 성공했습니다.", response);
    }

    // 보호자가 연결 신청한 리스트 확인
    @GetMapping("/api/care/user/guard/check-care-list")
    public Result checkGuardCareList(@CurrentMedicalUser MedicalUserPrincipal principal) {
        CheckCareListResponse response = careService.checkCareListByGuardUser(principal);
        return new Result<>("200", "연결 리스트 확인에 성공했습니다.", response);
    }
    
    // 피보호자는 승인
    @PostMapping("/api/care/user/change-care-approve")
    public Result changeCareApprove(@CurrentMedicalUser MedicalUserPrincipal principal, @RequestBody ChangeCareStateRequest request) {
        ChangeCareStateResponse response = careService.approveCare(principal, request);
        return new Result<>("200", "연결을 승인했습니다", response);
    }

    // 피보호자는 거절
    @PostMapping("/api/care/user/change-care-reject")
    public Result changeCareReject(@CurrentMedicalUser MedicalUserPrincipal principal, @RequestBody ChangeCareStateRequest request) {
        ChangeCareStateResponse response = careService.rejectCare(principal, request);
        return new Result<>("200", "연결을 거절했습니다.", response);
    }

    // 피보호자가 연결된 보호자를 조회하는 API
    @GetMapping("/api/care/user/wards")
    public Result searchGuardUsers(@CurrentMedicalUser MedicalUserPrincipal principal) {
        GuardSearchResponse response = careService.searchGuardUsers(principal);
        return new Result<>("200", "보호자 조회에 성공했습니다.", response);
    }

    // 보호자가 연결된 피보호자를 조회하는 API
    @GetMapping("/api/care/user/Guards")
    public Result searchWardUsers(@CurrentMedicalUser MedicalUserPrincipal principal) {
        WardSearchResponse response = careService.searchWardUsers(principal);
        return new Result<>("200", "피보호자 조회에 성공했습니다.", response);
    }

    // 메인 보호자 설정 API
    @PostMapping("/api/care/check-main-guard")
    public Result checkMainGuard(@CurrentMedicalUser MedicalUserPrincipal principal, @RequestBody CheckMainGuardRequest request) {
        CheckMainGuardResponse response = careService.checkMainGuard(principal, request);
        return new Result<>("200", "메인보호자 변경이 완료되었습니다.", response);
    }
    
    // 보호자 또는 피보호자 삭제 API
    @PostMapping("/api/care/delete-care")
    public Result deleteGuard(@CurrentMedicalUser MedicalUserPrincipal principal, @RequestBody DeleteCardRequest request) {
        careService.deleteCareUser(principal, request);
        
        return new Result<>("200", "선택하신 보호자가 삭제되었습니다.", null);
    }
    

}
