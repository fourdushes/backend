package tohear.hearo.user.ward;

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
import tohear.hearo.user.auth.service.CommonUserService;
import tohear.hearo.user.auth.service.UserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WardUserService implements UserService {

    private final WardUserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final CommonUserService commonUserService;

    @Override
    public boolean supports(UserType userType) {
        return userType == UserType.WARD;
    }

    @Override
    @Transactional
    public String join(JoinUserRequest request) {

        commonUserService.validateDuplicateUser(request.getId());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        WardUser user = new WardUser(request.getId(), request.getName(), request.getEmail(), encodedPassword, request.getUserType());
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
        WardUser user = userRepository.findById(request.getId()).orElseThrow(
            () -> new IllegalArgumentException("아이디가 올바르지 않습니다. "));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        UserType userType = user.getUserType();
        
        String token = tokenProvider.createToken(user.getId(), userType);
        return new LoginUserResponse(token, user.getId(), userType);
    }

    public WardUser findById(String id) {
        return userRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("아이디를 찾을 수 없습니다. " + id));
    }

    public List<WardUser> findAll() {
        return userRepository.findAll();
    }

    @Override
    public ToChangePasswordResponse validateToChangePassword(ToChangePasswordRequest request) {

        WardUser findUser = userRepository.findByEmail(request.getEmail()).orElseThrow(
            () -> new IllegalArgumentException("이메일이 올바르지 않습니다. " + request.getEmail()));

        if (!findUser.getName().equals(request.getName())) {
            throw new IllegalArgumentException("이름이 올바르지 않습니다. " + request.getName());
        }

        return new ToChangePasswordResponse(findUser.getId(), UserType.WARD);

    }

    @Override
    @Transactional
    public String changePassword(ChangePasswordRequest request) {

        WardUser findUser = userRepository.findById(request.getId()).orElseThrow(
            () -> new IllegalArgumentException("아이디가 올바르지 않습니다. " + request.getId()));

        if (!request.getNewPassword().equals(request.getCheckNewPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        findUser.changePassword(encodedNewPassword);

        return findUser.getId();
    }

}
