package tohear.hearo.care.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tohear.hearo.care.domain.Care;
import tohear.hearo.care.dto.request.ChangeCareStateRequest;
import tohear.hearo.care.dto.request.CheckMainGuardRequest;
import tohear.hearo.care.dto.request.DeleteCardRequest;
import tohear.hearo.care.dto.request.FindWardToCareRequest;
import tohear.hearo.care.dto.request.SaveCareRequest;
import tohear.hearo.care.dto.response.ChangeCareStateResponse;
import tohear.hearo.care.dto.response.CheckCareListDto;
import tohear.hearo.care.dto.response.CheckCareListResponse;
import tohear.hearo.care.dto.response.CheckMainGuardResponse;
import tohear.hearo.care.dto.response.FindWardToCareDto;
import tohear.hearo.care.dto.response.FindWardToCareResponse;
import tohear.hearo.care.dto.response.GuardSearchDto;
import tohear.hearo.care.dto.response.GuardSearchResponse;
import tohear.hearo.care.dto.response.SaveCareResponse;
import tohear.hearo.care.dto.response.WardSearchDto;
import tohear.hearo.care.dto.response.WardSearchResponse;
import tohear.hearo.care.repository.CareRepository;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;
import tohear.hearo.user.guardian.GuardUser;
import tohear.hearo.user.guardian.GuardUserRepository;
import tohear.hearo.user.ward.WardUser;
import tohear.hearo.user.ward.WardUserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareService {

    private final CareRepository careRepository;
    private final GuardUserRepository guardUserRepository;
    private final WardUserRepository wardUserRepository;

    // 보호자가 피보호자를 검색
    public FindWardToCareResponse findWardToCare(MedicalUserPrincipal principal, FindWardToCareRequest request, Pageable pageable) {

        if (principal.getUserType() != UserType.GUARDIAN) {
            throw new IllegalArgumentException("보호자만 검색할 수 있는 기능입니다.");
        }


        List<FindWardToCareDto> wardUserList = new ArrayList<>(); // FindWardToCareDto 객체를 담을 리스트 생성

        Page<WardUser> wardUserPage = careRepository.findWardUserToCare(request.getWardUserId(), pageable);

        for (WardUser wardUser : wardUserPage) {
            FindWardToCareDto wardSearchDto = new FindWardToCareDto(wardUser.getId(), 
                                                                    wardUser.getName());
            wardUserList.add(wardSearchDto);
        }

        return new FindWardToCareResponse(
            wardUserPage.getTotalElements(),
            wardUserPage.getNumber(),
            wardUserPage.getSize(),
            wardUserPage.hasNext(),
            wardUserList);
        
    }

    // 연결 저장
    @Transactional
    public SaveCareResponse saveCare(MedicalUserPrincipal principal, SaveCareRequest request) {

        if (principal.getUserType() != UserType.GUARDIAN) {
            throw new IllegalArgumentException("보호자만 연결을 신청할 수 있습니다.");
        }

        GuardUser guardUser = guardUserRepository.findById(principal.getUserId()).orElseThrow(() -> 
                                                            new IllegalArgumentException("보호자를 찾을 수 없습니다."));
        WardUser wardUser = wardUserRepository.findById(request.getWardUserId()).orElseThrow(() -> 
                                                            new IllegalArgumentException("피보호자를 찾을 수 없습니다."));

        if (careRepository.existsActiveCare(guardUser, wardUser)) {
            throw new IllegalArgumentException("이미 신청 되었거나 연결된 사용자입니다.");
        }

        Care care = new Care(wardUser, guardUser);
        careRepository.save(care);
        return new SaveCareResponse(care.getId());
    }

    // 연결 리스트 확인 - 피보호자 기준
    public CheckCareListResponse checkCareListByWardUser(MedicalUserPrincipal principal) {

        UserType userType = principal.getUserType();

        if (userType != UserType.WARD) {
            throw new IllegalArgumentException("피보호자만 연결 목록을 조회할 수 있습니다");
        }

        List<Care> careList = careRepository.findCareByWardUser(
                                wardUserRepository.findById(principal.getUserId()).orElseThrow(
                                    () -> new IllegalArgumentException("피보호자를 찾을 수 없습니다.")));
        
        List<CheckCareListDto> responseList = new ArrayList<>();
        for (Care care : careList) {
            CheckCareListDto response = new CheckCareListDto(
                care.getWardUser().getId(),
                care.getGuardUser().getId(),
                care.getCareState(),
                care.getCreatedAt(),
                care.getUpdatedAt()
            );
            responseList.add(response);
        }
        return new CheckCareListResponse(responseList.size(), responseList);
    }

    // 연결 리스트 확인 - 보호자 기준
    public CheckCareListResponse checkCareListByGuardUser(MedicalUserPrincipal principal) {

        UserType userType = principal.getUserType();

        if (userType != UserType.GUARDIAN) {
            throw new IllegalArgumentException("보호자만 연결 목록을 조회할 수 있습니다");
        }

        List<Care> careList = careRepository.findCareByGuardUser(
                                guardUserRepository.findById(principal.getUserId()).orElseThrow(
                                    () -> new IllegalArgumentException("보호자를 찾을 수 없습니다.")));
        
        List<CheckCareListDto> responseList = new ArrayList<>();
        for (Care care : careList) {
            CheckCareListDto response = new CheckCareListDto(
                care.getWardUser().getId(),
                care.getGuardUser().getId(),
                care.getCareState(),
                care.getCreatedAt(),
                care.getUpdatedAt()
            );
            responseList.add(response);
        }
        return new CheckCareListResponse(responseList.size(), responseList);
    }
    

    // 연결 승인
    @Transactional
    public ChangeCareStateResponse approveCare(MedicalUserPrincipal principal, ChangeCareStateRequest request) {

        if (principal.getUserType() != UserType.WARD) {
            throw new IllegalArgumentException("피보호자만 연결 요청을 변경할 수 있습니다.");
        }

        WardUser findWardUser = wardUserRepository.findById(principal.getUserId()).orElseThrow(() 
                                        -> new IllegalArgumentException("피보호자를 찾을 수 없습니다."));

        Care findCare = careRepository.findByIdAndWardUser_Id(request.getCareId(),principal.getUserId()).orElseThrow(
            () -> new IllegalArgumentException("연결 요청을 찾을 수 없거나 변경 권한이 없습니다."));

        findCare.approve();
        boolean existMainGuard = careRepository.existMainGuard(findWardUser); // 피보호자가 설정하는 첫번째 보호자인지 확인

        if (!existMainGuard) {
            findCare.changeMainGuard(); // 첫번째 보호자라면 메인 보호자 설정
        }

        return new ChangeCareStateResponse(findCare.getId(), findCare.getCareState());
    }

    // 연결 거절
    @Transactional
    public ChangeCareStateResponse rejectCare(MedicalUserPrincipal principal, ChangeCareStateRequest request) {

        if (principal.getUserType() != UserType.WARD) {
            throw new IllegalArgumentException("피보호자만 연결 요청을 변경할 수 있습니다.");
        }

        Care findCare = careRepository.findByIdAndWardUser_Id(request.getCareId(),principal.getUserId()).orElseThrow(
            () -> new IllegalArgumentException("연결 요청을 찾을 수 없거나 변경 권한이 없습니다."));

        findCare.reject();
        return new ChangeCareStateResponse(findCare.getId(), findCare.getCareState());
    }

    // 보호자가 피보호자를 조회
    public WardSearchResponse searchWardUsers(MedicalUserPrincipal principal) {

        if (principal.getUserType() != UserType.GUARDIAN) {
            throw new IllegalArgumentException("보호자만 피보호자를 조회할 수 있습니다.");
        }


        GuardUser findGuardUser = guardUserRepository.findById(principal.getUserId()).orElseThrow(() 
                                        -> new IllegalArgumentException("보호자를 찾을 수 없습니다."));


        List<WardSearchDto> wardUserList = careRepository.findWardUser(findGuardUser); // 보호자가 보호하고 있는 피보호자 리스트 뽑기

        return new WardSearchResponse(wardUserList.size(), wardUserList);
        
    }

    // 피보호자가 보호자를 조회
    public GuardSearchResponse searchGuardUsers(MedicalUserPrincipal principal) {

        if (principal.getUserType() != UserType.WARD) {
            throw new IllegalArgumentException("피보호자만 보호자를 조회할 수 있습니다.");
        }

        WardUser findWardUser = wardUserRepository.findById(principal.getUserId()).orElseThrow(() 
                                        -> new IllegalArgumentException("피보호자를 찾을 수 없습니다."));


        List<GuardSearchDto> guardUserList = careRepository.findGuardUser(findWardUser); // 피보호자가 본인의 보호자 리스틑 뽑기


        return new GuardSearchResponse(guardUserList.size(), guardUserList);
        
    }

    // 메인 보호자가 제거 후 메인 보호자 재설정
    @Transactional
    public CheckMainGuardResponse checkMainGuard(MedicalUserPrincipal principal, CheckMainGuardRequest request) {

        if (principal.getUserType() != UserType.WARD) {
            throw new IllegalArgumentException("피보호자만 본인의 메인 보호자를 설정할 수 있습니다.");
        }

        WardUser wardUser = wardUserRepository.findById(principal.getUserId()).orElseThrow(() 
                                        ->  new IllegalArgumentException("피보호자를 찾을 수 없습니다."));

         GuardUser guardUser = guardUserRepository.findById(request.getChangeGuardUserId()).orElseThrow(() 
                                        -> new IllegalArgumentException("보호자를 찾을 수 없습니다."));


        Care deleteMainCare = careRepository.findMainGuard(wardUser).orElseThrow(() 
            -> new IllegalArgumentException("메인 보호자 또는 피보호자를 찾을 수 없습니다."));

        deleteMainCare.deleteMainGuard(); // 기존 메인 보호자 취소

        Care changeMainCare = careRepository.findChangeMainGuard(wardUser, guardUser).orElseThrow(() 
            -> new IllegalArgumentException("변경될 메인 보호자 또는 피보호자를 찾을 수 없습니다."));

        changeMainCare.changeMainGuard(); // 메인 보호자 재설정

        return new CheckMainGuardResponse(deleteMainCare.getId(), changeMainCare.getId());
    }

    @Transactional
    public void deleteCareUser(MedicalUserPrincipal principal, DeleteCardRequest request) {

        if (principal.getUserType() != UserType.WARD && principal.getUserType() != UserType.GUARDIAN) {
            throw new IllegalArgumentException("피보호자, 보호자만 케어를 삭제할 수 있습니다.");
        }

        Care care = careRepository.findById(request.getDeleteCareId()).orElseThrow(()
            -> new IllegalArgumentException("케어를 찾을 수 없습니다."));

        boolean isWardOwner = principal.getUserType() == UserType.WARD && care.getWardUser().getId().equals(principal.getUserId());

        boolean isGuardOwner = principal.getUserType() == UserType.GUARDIAN && care.getGuardUser().getId().equals(principal.getUserId());

        if (!isWardOwner && !isGuardOwner) {
            throw new IllegalArgumentException("삭제 권한이 없는 보호 관계입니다.");
        }
        
        careRepository.deleteById(care.getId());
    }

}
