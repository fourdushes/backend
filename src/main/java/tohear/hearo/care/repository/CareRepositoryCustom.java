package tohear.hearo.care.repository;

import java.util.List;

import tohear.hearo.care.domain.Care;
import tohear.hearo.user.guardian.GuardUser;
import tohear.hearo.user.ward.WardUser;

public interface CareRepositoryCustom {

    List<WardUser> findWardUser(GuardUser guardUser); // 보호자가 피보호자를 조회
    List<GuardUser> findGuardUser(WardUser wardUser); // 피보호자가 보호자를 조회
    List<WardUser> findWardUserToCare(String wardUserId); // 보호자가 피보호자와 매칭되기 위해 피보호자를 검색
    List<Care> findCareByGuardUser(GuardUser guardUser); // 보호자가 연결을 신청한 Care 조회
    List<Care> findCareByWardUser(WardUser wardUser); // 피보호자가 연결을 신청한 Care 조회

}
