package tohear.hearo.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.JwtTokenProvider;
import tohear.hearo.user.domain.User;
import tohear.hearo.user.domain.dto.response.LoginUserResponse;
import tohear.hearo.user.repository.GuardUserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuardUserService {

    private final GuardUserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public String join(User user) { // 회원 가입
        validateDuplicateUser(user.getId());
        userRepository.save(user);
        return user.getId();
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("아이디를 찾을 수 없습니다. " + id));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public String findId(String name, String email) {
        return userRepository.findIdByNameAndEmail(name, email);
    }

    public LoginUserResponse validateLogin(String id, String password) { // 로그인 검증
        User user = userRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("아이디가 옳바르지 않습니다. " + id)); // 아이디가 존재하는지 확인
        
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 옳바르지 않습니다."); // 비밀번호 검증
        }

        String token = tokenProvider.createToken(user.getId()); // 로그인 인증 토큰 생성
        return new LoginUserResponse(token, user.getId());
    }

    public void validateDuplicateUser(String id) { // 중복 회원 검증
        if (userRepository.findById(id).isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다."); // 아이디가 이미 존재하는 경우 예외 처리
        }
    }

}
