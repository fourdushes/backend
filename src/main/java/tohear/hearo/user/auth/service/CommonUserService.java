package tohear.hearo.user.auth.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.guardian.GuardUserRepository;
import tohear.hearo.user.institution.InstitutionsUserRepository;
import tohear.hearo.user.ward.WardUserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonUserService {

    private final WardUserRepository wardUserRepository;
    private final GuardUserRepository guardUserRepository;
    private final InstitutionsUserRepository institutionsUserRepository;

    // 로그인하기 전에 사용자 유형을 확인
    public UserType checkUserTypeById(String userId) {

        if (wardUserRepository.existsById(userId)) {
            return UserType.WARD; // 피보호자 사용자
        } else if (guardUserRepository.existsById(userId)) {
            return UserType.GUARDIAN; // 보호자 사용자
        } else if (institutionsUserRepository.existsById(userId)) {
            return UserType.INSTITUTIONS; // 기관 사용자
        } else {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");

        }
    }

    // 아이디 찾기 전에 사용자 유형을 확인
    public UserType checkUserTypeByEmail(String email) {

        if (wardUserRepository.existsByEmail(email)) {
            return UserType.WARD; // 피보호자 사용자
        } else if (guardUserRepository.existsByEmail(email)) {
            return UserType.GUARDIAN; // 보호자 사용자
        } else if (institutionsUserRepository.existsByEmail(email)) {
            return UserType.INSTITUTIONS; // 기관 사용자
        } else {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");

        }
    }

}
