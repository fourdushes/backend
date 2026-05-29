package tohear.hearo.user.institution;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.JwtTokenProvider;
import tohear.hearo.user.auth.dto.response.LoginUserResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstitutionsUserService {

    private final InstitutionsUserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public String join(InstitutionsUser user) { // 회원 가입
        validateDuplicateUser(user.getId());
        userRepository.save(user);
        return user.getId();
    }

    public InstitutionsUser findById(String id) {
        return userRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("아이디를 찾을 수 없습니다. " + id));
    }

    public List<InstitutionsUser> findAll() {
        return userRepository.findAll();
    }

    public String findId(String name, String email) {
        return userRepository.findIdByNameAndEmail(name, email);
    }

    public LoginUserResponse validateLogin(String id, String password) { // 로그인 검증
        InstitutionsUser user = userRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("아이디가 옳바르지 않습니다. " + id));
        
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 옳바르지 않습니다.");
        }

        String token = tokenProvider.createToken(user.getId());
        return new LoginUserResponse(token, user.getId());
    }

    public void validateDuplicateUser(String id) { // 중복 회원 검증
        if (userRepository.findById(id).isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

}
