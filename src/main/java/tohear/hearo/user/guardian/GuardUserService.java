package tohear.hearo.user.guardian;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.JwtTokenProvider;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.auth.dto.request.ChangePasswordRequest;
import tohear.hearo.user.auth.dto.request.IdFindRequest;
import tohear.hearo.user.auth.dto.request.JoinUserRequest;
import tohear.hearo.user.auth.dto.request.LoginUserRequest;
import tohear.hearo.user.auth.dto.request.ToChangePasswordRequest;
import tohear.hearo.user.auth.dto.response.LoginUserResponse;
import tohear.hearo.user.auth.dto.response.ToChangePasswordResponse;
import tohear.hearo.user.auth.service.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuardUserService implements UserService {

    private final GuardUserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;


    @Override
    public boolean supports(UserType userType) {
        return userType == UserType.GUARDIAN;
    }

    @Override
    @Transactional
    public String join(JoinUserRequest request) {

        validateDuplicateUser(request.getId());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        GuardUser user = new GuardUser(request.getId(), request.getName(), request.getEmail(), encodedPassword, request.getUserType());
        userRepository.save(user);
        return user.getId();
    }

    @Override
    public String findId(IdFindRequest request) {
        return userRepository.findIdByNameAndEmail(request.getName(), request.getEmail()).orElseThrow(
            () -> new IllegalArgumentException("아이디를 찾을 수 없습니다."));
    }

    

    @Override
    @Transactional
    public LoginUserResponse validateLogin(LoginUserRequest request) { // 로그인 검증
        GuardUser user = userRepository.findById(request.getId()).orElseThrow(
            () -> new IllegalArgumentException("아이디가 올바르지 않습니다. "));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        UserType userType = user.getUserType();

        String token = tokenProvider.createToken(user.getId(), userType);
        return new LoginUserResponse(token, user.getId(), userType);
    }

    @Override
    public void validateDuplicateUser(String id) { // 중복 회원 검증
        if (userRepository.findById(id).isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public GuardUser findById(String id) {
        return userRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("아이디를 찾을 수 없습니다. " + id));
    }

    public List<GuardUser> findAll() {
        return userRepository.findAll();
    }

    @Override
    public ToChangePasswordResponse validateToChangePassword(ToChangePasswordRequest request) {

       GuardUser findUser = userRepository.findByEmail(request.getEmail()).orElseThrow(
            () -> new IllegalArgumentException("이메일이 올바르지 않습니다. " + request.getEmail()));

        if (findUser.getName().equals(request.getName()) == false) {
            throw new IllegalArgumentException("이름이 올바르지 않습니다. " + request.getName());
        }

        return new ToChangePasswordResponse(findUser.getId(), UserType.GUARDIAN);

    }

    @Override
    public String changePassword(ChangePasswordRequest request) {

        GuardUser findUser = userRepository.findById(request.getId()).orElseThrow(
            () -> new IllegalArgumentException("아이디가 올바르지 않습니다. " + request.getId()));

        if (request.getNewPassword().equals(request.getCheckNewPassword()) == false) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        findUser.changePassword(request.getNewPassword());

        return findUser.getId();
    }
}
