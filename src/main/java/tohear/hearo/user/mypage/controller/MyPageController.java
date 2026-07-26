package tohear.hearo.user.mypage.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.response.Result;
import tohear.hearo.user.auth.principal.CurrentMedicalUser;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;
import tohear.hearo.user.mypage.dto.request.ChangeNameRequest;
import tohear.hearo.user.mypage.dto.response.ChangeNameResponse;
import tohear.hearo.user.mypage.dto.response.GuardMyPageResponse;
import tohear.hearo.user.mypage.dto.response.InstitutionsMyPageResponse;
import tohear.hearo.user.mypage.dto.response.WardMyPageResponse;
import tohear.hearo.user.mypage.service.MyPageService;


@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/ward-user")
    public Result wardUserMyPage(@CurrentMedicalUser MedicalUserPrincipal principal) {
        WardMyPageResponse response = myPageService.wardMyPage(principal);
        return new Result<>("200", "피보호자 마이페이지가 정상적으로 실행됩니다.", response);
    }

    @GetMapping("/guard-user")
    public Result guardUserMyPage(@CurrentMedicalUser MedicalUserPrincipal principal) {
        GuardMyPageResponse response = myPageService.guardMyPage(principal);
        return new Result<>("200", "보호자 마이페이지가 정상적으로 실행됩니다.", response);
    }

    @GetMapping("/institutions-user")
    public Result institutionsUserMyPage(@CurrentMedicalUser MedicalUserPrincipal principal) {
        InstitutionsMyPageResponse response = myPageService.institutionsMyPage(principal);
        return new Result<>("200", "기관 마이페이지가 정상적으로 실행됩니다.", response);
    }
    
    @PatchMapping("/change-name")
    public Result changeName(@CurrentMedicalUser MedicalUserPrincipal principal, @Valid @RequestBody ChangeNameRequest request) {
        ChangeNameResponse response = myPageService.changeName(principal, request);
        return new Result<>("200", "이름이 변경되었습니다.", response);
    }

}
