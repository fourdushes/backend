package tohear.hearo.care.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.care.dto.request.ChangeCareStateRequest;
import tohear.hearo.care.dto.request.FindWardToCareRequest;
import tohear.hearo.care.dto.request.GuardCheckCareListRequest;
import tohear.hearo.care.dto.request.GuardSearchRequest;
import tohear.hearo.care.dto.request.SaveCareRequest;
import tohear.hearo.care.dto.request.WardCheckCareListRequest;
import tohear.hearo.care.dto.request.WardSearchRequest;
import tohear.hearo.care.dto.response.ChangeCareStateResponse;
import tohear.hearo.care.dto.response.CheckCareListResponse;
import tohear.hearo.care.dto.response.FindWardToCareResponse;
import tohear.hearo.care.dto.response.GuardSearchResponse;
import tohear.hearo.care.dto.response.SaveCareResponse;
import tohear.hearo.care.dto.response.WardSearchResponse;
import tohear.hearo.care.service.CareService;
import tohear.hearo.global.Result;



@RestController
@RequiredArgsConstructor
public class CareController {

    private final CareService careService;

    // 보호자가 피보호자를 검색하는 API
    @GetMapping("/api/care/user/search-ward-user")
    public Result searchWardUserToCare(@ModelAttribute FindWardToCareRequest request) {
        FindWardToCareResponse response = careService.findWardToCare(request);
        return new Result<>("200", "피보호자 검색이 성공했습니다.", response);
    }

    // 보호자와 피보호자를 연결하는 API
    @PostMapping("/api/care/user/save-care")
    public Result saveCare(@RequestBody SaveCareRequest request) {
        SaveCareResponse response = careService.saveCare(request);
        return new Result<>("200", "연결 저장에 성공했습니다.", response);
    }
    
    // 피보호자가 연결 리스트 확인
    @GetMapping("/api/care/user/ward/check-care-list")
    public Result checkCareList(@ModelAttribute WardCheckCareListRequest wardRequest) {
        CheckCareListResponse response = careService.checkCareListByWardUser(wardRequest);
        return new Result<>("200", "연결 리스트 확인에 성공했습니다.", response);
    }

    // 보호자가 연결 리스트 확인
    @GetMapping("/api/care/user/guard/check-care-list")
    public Result checkCareList(@ModelAttribute GuardCheckCareListRequest guardRequest) {
        CheckCareListResponse response = careService.checkCareListByGuardUser(guardRequest);
        return new Result<>("200", "연결 리스트 확인에 성공했습니다.", response);
    }
    
    // 피보호자는 승인
    @PostMapping("/api/care/user/change-care-approve")
    public Result changeCareApprove(@RequestBody ChangeCareStateRequest request) {
        ChangeCareStateResponse response = careService.approveCare(request);
        return new Result<>("200", "연결을 승인했습니다", response);
    }

    // 피보호자는 거절
    @PostMapping("/api/care/user/change-care-reject")
    public Result changeCareReject(@RequestBody ChangeCareStateRequest request) {
        ChangeCareStateResponse response = careService.rejectCare(request);
        return new Result<>("200", "연결을 거절했습니다.", response);
    }

    // 피보호자가 보호자를 검색하는 API
    @GetMapping("/api/care/user/wards")
    public Result searchWardUser(@ModelAttribute WardSearchRequest request) {
        WardSearchResponse response = careService.searchWardUsers(request);
        return new Result<>("200", "피보호자 검색이 성공했습니다.", response);
    }

    // 피보호자가 보호자를 검색하는 API
    @GetMapping("/api/care/user/Guards")
    public Result searchGuardUser(@ModelAttribute GuardSearchRequest request) {
        GuardSearchResponse response = careService.searchGuardUsers(request);
        return new Result<>("200", "보호자 검색이 성공했습니다.", response);
    }


}
