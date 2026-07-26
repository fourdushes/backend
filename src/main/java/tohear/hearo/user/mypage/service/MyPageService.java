package tohear.hearo.user.mypage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;
import tohear.hearo.user.guardian.GuardUser;
import tohear.hearo.user.guardian.GuardUserService;
import tohear.hearo.user.institution.InstitutionsUser;
import tohear.hearo.user.institution.InstitutionsUserService;
import tohear.hearo.user.mypage.dto.request.ChangeNameRequest;
import tohear.hearo.user.mypage.dto.response.ChangeNameResponse;
import tohear.hearo.user.mypage.dto.response.GuardMyPageResponse;
import tohear.hearo.user.mypage.dto.response.InstitutionsMyPageResponse;
import tohear.hearo.user.mypage.dto.response.WardMyPageResponse;
import tohear.hearo.user.ward.WardUser;
import tohear.hearo.user.ward.WardUserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final WardUserService wardUserService;
    private final GuardUserService guardUserService;
    private final InstitutionsUserService institutionsUserService;

    public WardMyPageResponse wardMyPage(MedicalUserPrincipal principal) {

        if (principal.getUserType() != UserType.WARD) {
            throw new IllegalArgumentException("피보호자만 볼 수 있는 기능입니다.");
        }

        WardUser wardUser = wardUserService.findById(principal.getUserId());

        return new WardMyPageResponse(
            wardUser.getId(),
            wardUser.getName(),
            wardUser.getEmail(),
            wardUser.getUserType()
        );
    }

    public GuardMyPageResponse guardMyPage(MedicalUserPrincipal principal) {

        if (principal.getUserType() != UserType.GUARDIAN) {
            throw new IllegalArgumentException("보호자만 볼 수 있는 기능입니다.");
        }

        GuardUser guardUser = guardUserService.findById(principal.getUserId());

        return new GuardMyPageResponse(
            guardUser.getId(),
            guardUser.getName(),
            guardUser.getEmail(),
            guardUser.getUserType()
        );
    }

    public InstitutionsMyPageResponse institutionsMyPage(MedicalUserPrincipal principal) {

        if (principal.getUserType() != UserType.INSTITUTIONS) {
            throw new IllegalArgumentException("기관만 볼 수 있는 기능입니다.");
        }

        InstitutionsUser institutionsUser = institutionsUserService.findById(principal.getUserId());

        return new InstitutionsMyPageResponse(
            institutionsUser.getId(),
            institutionsUser.getName(),
            institutionsUser.getEmail(),
            institutionsUser.getUserType(),
            institutionsUser.getInstitutionsId()
        );
    }

    @Transactional
    public ChangeNameResponse changeName(MedicalUserPrincipal principal, ChangeNameRequest request) {
        if (principal.getUserType() == UserType.WARD) {
            WardUser wardUser = wardUserService.findById(principal.getUserId());
            wardUser.changeName(request.getNewName());
            return new ChangeNameResponse(request.getNewName());
        }

        if (principal.getUserType() == UserType.GUARDIAN) {
            GuardUser guardUser = guardUserService.findById(principal.getUserId());
            guardUser.changeName(request.getNewName());
            return new ChangeNameResponse(request.getNewName());
        }

        if (principal.getUserType() == UserType.INSTITUTIONS) {
            InstitutionsUser institutionsUser = institutionsUserService.findById(principal.getUserId());
            institutionsUser.changeName(request.getNewName());
            return new ChangeNameResponse(request.getNewName());
        }

        throw new IllegalArgumentException("이름을 변경할 대상을 찾을 수 없습니다.");
    }


}
