package tohear.hearo.care.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tohear.hearo.care.domain.Care;
import tohear.hearo.care.dto.response.GuardSearchDto;
import tohear.hearo.care.dto.response.WardSearchDto;
import tohear.hearo.user.guardian.GuardUser;
import tohear.hearo.user.ward.WardUser;

public interface CareRepositoryCustom {

    List<WardSearchDto> findWardUser(GuardUser guardUser); // 보호자가 피보호자를 조회
    List<GuardSearchDto> findGuardUser(WardUser wardUser); // 피보호자가 보호자를 조회
    Page<WardUser> findWardUserToCare(String wardUserId, Pageable pageable); // 보호자가 피보호자와 매칭되기 위해 피보호자를 검색
    List<Care> findCareByGuardUser(GuardUser guardUser); // 보호자가 연결을 신청한 Care 조회
    List<Care> findCareByWardUser(WardUser wardUser); // 피보호자가 연결을 신청한 Care 조회
    boolean existsActiveCare(GuardUser guardUser, WardUser wardUser); // 중복 연결을 막기 위해 조회
    List<Care> findByUserId(String id); // 피보호자 유저 아이디로 보호자들 조회
    boolean existMainGuard(WardUser wardUser); // 메인 보호자가 설정되어 있는지 확인
    Optional<Care> findMainGuard(WardUser wardUser); // 현재 메인 보호자가 누구인지 확인
    Optional<Care> findChangeMainGuard(WardUser wardUser, GuardUser guardUser); // 변경될 메인 보호자 케어 아이디 찾기

}
