package tohear.hearo.institution.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.response.Result;
import tohear.hearo.institution.auth.CurrentInstitution;
import tohear.hearo.institution.auth.InstitutionPrincipal;
import tohear.hearo.institution.dto.request.ChangeStateRequest;
import tohear.hearo.institution.dto.request.IdFindRequest;
import tohear.hearo.institution.dto.request.InstitutionChangePasswordRequest;
import tohear.hearo.institution.dto.request.InstitutionJoinRequest;
import tohear.hearo.institution.dto.request.InstitutionLoginRequest;
import tohear.hearo.institution.dto.request.InstitutionToChangePasswordRequest;
import tohear.hearo.institution.dto.request.JudgeUserRequest;
import tohear.hearo.institution.dto.request.SearchInstitutionUserRequest;
import tohear.hearo.institution.dto.response.ChangeStateResponse;
import tohear.hearo.institution.dto.response.InstitutionJoinResponse;
import tohear.hearo.institution.dto.response.InstitutionLoginResponse;
import tohear.hearo.institution.dto.response.InstitutionToChangePasswordResponse;
import tohear.hearo.institution.dto.response.JudgeUserResponse;
import tohear.hearo.institution.service.InstitutionService;
import tohear.hearo.user.auth.dto.response.InstitutionSearchResponse;
import tohear.hearo.user.auth.mail.MailService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/institutions")
public class InstitutionController {

    private final InstitutionService institutionService;
    private final MailService mailService;


    @GetMapping("/search")
    public Page<InstitutionSearchResponse> search(@RequestParam(defaultValue = "") String keyword,
                                                  @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return institutionService.search(keyword, pageable);
    }

    @PostMapping("/join")
    public Result join(@RequestBody InstitutionJoinRequest request) {
        mailService.validateVerifiedEmail(request.getEmail());
        institutionService.validateEmailAvailable(request.getEmail());

        InstitutionJoinResponse response = institutionService.join(request);
        return new Result<>("200", "정상적으로 기관 회원가입이 완료되었습니다.", response);
    }

    @PostMapping("/login")
    public Result login(@RequestBody InstitutionLoginRequest request) {
        InstitutionLoginResponse response = institutionService.validateLogin(request);
        return new Result<>("200", "로그인에 성공하였습니다.", response);
    }

    @PostMapping("/id-find")
    public Result<String> findId(@Valid @RequestBody IdFindRequest request) {
        String userId = institutionService.findId(request);
        return new Result<>("200", "아이디를 찾았습니다.", userId);
    }

    @PostMapping("/to-change-password")
    public Result<InstitutionToChangePasswordResponse> toChangePassword(@Valid @RequestBody InstitutionToChangePasswordRequest request) {
        InstitutionToChangePasswordResponse response =institutionService.validateToChangePassword(request);
        return new Result<>("200", "인증에 성공했습니다.", response);
    }

    @PostMapping("/change-password")
    public Result<Long> changePassword(@Valid @RequestBody InstitutionChangePasswordRequest request) {
        Long institutionId = institutionService.changePassword(request);
        return new Result<>("200", "비밀번호 변경에 성공했습니다.", institutionId);
    }

    @PostMapping("/search-pending-user")
    public Result searchPending(@CurrentInstitution InstitutionPrincipal principal, @RequestBody JudgeUserRequest request,
                                @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        JudgeUserResponse response = institutionService.searchPending(principal.getInstitutionId(), request, pageable);
        return new Result<>("200", "승인 대기중인 기관 유저를 조회했습니다", response);
    }

    @PostMapping("/search-approved-user")
    public Result searchApproved(@CurrentInstitution InstitutionPrincipal principal, @RequestBody JudgeUserRequest request,
                                @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        JudgeUserResponse response = institutionService.searchApproved(principal.getInstitutionId(), request, pageable);
        return new Result<>("200", "승인 대기중인 기관 유저를 조회했습니다", response);
    }

    @PostMapping("/search-reject-user")
    public Result searchReject(@CurrentInstitution InstitutionPrincipal principal, @RequestBody JudgeUserRequest request,
                                @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        JudgeUserResponse response = institutionService.searchReject(principal.getInstitutionId(), request, pageable);
        return new Result<>("200", "승인 대기중인 기관 유저를 조회했습니다", response);
    }

    @PostMapping("/user/approved")
    public Result approved(@CurrentInstitution InstitutionPrincipal principal, @RequestBody ChangeStateRequest request) {
        ChangeStateResponse response = institutionService.approvedState(principal.getInstitutionId(), request);
        return new Result<>("200", "승인 되었습니다", response);
    }

    @PostMapping("/user/reject")
    public Result reject(@CurrentInstitution InstitutionPrincipal principal, @RequestBody ChangeStateRequest request) {
        ChangeStateResponse response = institutionService.rejectState(principal.getInstitutionId(), request);
        return new Result<>("200", "거절 되었습니다", response);
    }

    @PostMapping("/user/delete")
    public Result delete(@CurrentInstitution InstitutionPrincipal principal, @RequestBody ChangeStateRequest request) {
        ChangeStateResponse response = institutionService.deleteState(principal.getInstitutionId(), request);
        return new Result<>("200", "삭제 되었습니다", response);
    }

    @GetMapping("/search/institution-user")
    public Result getMethodName(@CurrentInstitution InstitutionPrincipal principal,
                                @ModelAttribute SearchInstitutionUserRequest request,
                                @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        JudgeUserResponse response = institutionService.searchInstitutionUser(principal.getInstitutionId(), request, pageable);
        return new Result<>("200", "조회 성공", response);
    }


}
