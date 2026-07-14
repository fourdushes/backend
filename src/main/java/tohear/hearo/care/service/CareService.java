package tohear.hearo.care.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tohear.hearo.care.domain.Care;
import tohear.hearo.care.dto.request.ChangeCareStateRequest;
import tohear.hearo.care.dto.request.FindWardToCareRequest;
import tohear.hearo.care.dto.request.GuardCheckCareListRequest;
import tohear.hearo.care.dto.request.GuardSearchRequest;
import tohear.hearo.care.dto.request.SaveCareRequest;
import tohear.hearo.care.dto.request.WardCheckCareListRequest;
import tohear.hearo.care.dto.request.WardSearchRequest;
import tohear.hearo.care.dto.response.ChangeCareStateResponse;
import tohear.hearo.care.dto.response.CheckCareListDto;
import tohear.hearo.care.dto.response.CheckCareListResponse;
import tohear.hearo.care.dto.response.FindWardToCareDto;
import tohear.hearo.care.dto.response.FindWardToCareResponse;
import tohear.hearo.care.dto.response.GuardSearchDto;
import tohear.hearo.care.dto.response.GuardSearchResponse;
import tohear.hearo.care.dto.response.SaveCareResponse;
import tohear.hearo.care.dto.response.WardSearchDto;
import tohear.hearo.care.dto.response.WardSearchResponse;
import tohear.hearo.care.repository.CareRepository;
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
    public FindWardToCareResponse findWardToCare(FindWardToCareRequest request) {

        List<FindWardToCareDto> wardUserList = new ArrayList<>(); // FindWardToCareDto 객체를 담을 리스트 생성

        List<WardUser> WardUserList = careRepository.findWardUserToCare(request.getWardUserId());

        for (WardUser wardUser : WardUserList) {
            FindWardToCareDto wardSearchDto = new FindWardToCareDto(wardUser.getId(), 
                                                                    wardUser.getName());
            wardUserList.add(wardSearchDto);
        }

        return new FindWardToCareResponse(wardUserList.size(), wardUserList);
        
    }

    // 연결 저장
    public SaveCareResponse saveCare(SaveCareRequest request) {
        GuardUser guardUser = guardUserRepository.findById(request.getGuardUserId()).orElseThrow(() -> 
                                                            new IllegalArgumentException("보호자를 찾을 수 없습니다."));
        WardUser wardUser = wardUserRepository.findById(request.getWardUserId()).orElseThrow(() -> 
                                                            new IllegalArgumentException("피보호자를 찾을 수 없습니다."));
        Care care = new Care(wardUser, guardUser);
        careRepository.save(care);
        return new SaveCareResponse(care.getId());
    }

    // 연결 리스트 확인 - 피보호자 기준
    public CheckCareListResponse checkCareListByWardUser(WardCheckCareListRequest request) {
        List<Care> careList = careRepository.findCareByWardUser(
                                wardUserRepository.findById(request.getWardUserId()).orElseThrow(() -> new IllegalArgumentException("피보호자를 찾을 수 없습니다.")));
        
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
    public CheckCareListResponse checkCareListByGuardUser(GuardCheckCareListRequest request) {
        List<Care> careList = careRepository.findCareByGuardUser(
                                guardUserRepository.findById(request.getGuardUserId()).orElseThrow(() -> new IllegalArgumentException("보호자를 찾을 수 없습니다.")));
        
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
    public ChangeCareStateResponse approveCare(ChangeCareStateRequest request) {
        Care findCare = careRepository.findById(request.getCareId()).orElseThrow(() -> 
                                new IllegalArgumentException("보호자 연결 요청을 찾을 수 없습니다."));
        findCare.approve();
        return new ChangeCareStateResponse(findCare.getId(), findCare.getCareState());
    }

    // 연결 거절
    public ChangeCareStateResponse rejectCare(ChangeCareStateRequest request) {
        Care findCare = careRepository.findById(request.getCareId()).orElseThrow(() -> 
                                new IllegalArgumentException("보호자 연결 요청을 찾을 수 없습니다."));
        findCare.reject();
        return new ChangeCareStateResponse(findCare.getId(), findCare.getCareState());
    }

    // 보호자가 피보호자를 조회
    public WardSearchResponse searchWardUsers(WardSearchRequest request) {

        List<WardSearchDto> wardSearchList = new ArrayList<>(); // WardSearchDto 객체를 담을 리스트 생성

        GuardUser findGuardUser = guardUserRepository.findById(request.getGuardUserId()).orElseThrow(() 
                                        -> new IllegalArgumentException("보호자를 찾을 수 없습니다."));


        List<WardUser> WardUserList = careRepository.findWardUser(findGuardUser); // 보호자가 보호하고 있는 피보호자 리스트 뽑기

        // Care 엔티티에서 WardUser의 정보를 추출하여 WardSearchDto 객체를 생성하고 리스트 만들기
        for (WardUser wardUser : WardUserList) {
            WardSearchDto wardSearchDto = new WardSearchDto(wardUser.getId(), 
                                                            wardUser.getName(), 
                                                            wardUser.getUserType());
            wardSearchList.add(wardSearchDto);
        }

        return new WardSearchResponse(wardSearchList.size(), wardSearchList);
        
    }

    // 피보호자가 보호자를 조회
    public GuardSearchResponse searchGuardUsers(GuardSearchRequest request) {

        List<GuardSearchDto> guardSearchList = new ArrayList<>(); // GuardSearchDto 객체를 담을 리스트 생성

        WardUser findWardUser = wardUserRepository.findById(request.getWardUserId()).orElseThrow(() 
                                        -> new IllegalArgumentException("피보호자를 찾을 수 없습니다."));


        List<GuardUser> GuardUserList = careRepository.findGuardUser(findWardUser); // 피보호자가 본인의 보호자 리스틑 뽑기

        // Care 엔티티에서 GuardUser의 정보를 추출하여 GuardSearchDto 객체를 생성하고 리스트 만들기
        for (GuardUser guardUser : GuardUserList) {
            GuardSearchDto guardSearchDto = new GuardSearchDto(guardUser.getId(), 
                                                               guardUser.getName(), 
                                                               guardUser.getUserType());
            guardSearchList.add(guardSearchDto);
        }

        return new GuardSearchResponse(guardSearchList.size(), guardSearchList);
        
    }

}
