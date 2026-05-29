package tohear.hearo.user.institution;

import java.util.List;

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
public class InstitutionsUserService implements UserService {

    private final InstitutionsUserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

     @Override
    public boolean supports(UserType userType) {
        return userType == UserType.INSTITUTIONS;
    }

    @Override
    @Transactional
    public String join(JoinUserRequest request) {

        validateDuplicateUser(request.getId());
        InstitutionsUser user = new InstitutionsUser(request.getId(), request.getName(), request.getEmail(), request.getPassword(), request.getUserType());
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
        InstitutionsUser user = userRepository.findById(request.getId()).orElseThrow(
            () -> new IllegalArgumentException("아이디가 올바르지 않습니다. "));
        
        if (!user.getPassword().equals(request.getPassword())) {
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

    public InstitutionsUser findById(String id) {
        return userRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("아이디를 찾을 수 없습니다. " + id));
    }

    public List<InstitutionsUser> findAll() {
        return userRepository.findAll();
    }

    @Override
    public ToChangePasswordResponse validateToChangePassword(ToChangePasswordRequest request) {

        InstitutionsUser findUser = userRepository.findByEmail(request.getEmail()).orElseThrow(
            () -> new IllegalArgumentException("이메일이 올바르지 않습니다. " + request.getEmail()));

        if (findUser.getName().equals(request.getName()) == false) {
            throw new IllegalArgumentException("이름이 올바르지 않습니다. " + request.getName());
        }


        return new ToChangePasswordResponse(findUser.getId(), UserType.INSTITUTIONS);

    }

    @Override
    public String changePassword(ChangePasswordRequest request) {

        InstitutionsUser findUser = userRepository.findById(request.getId()).orElseThrow(
            () -> new IllegalArgumentException("아이디가 올바르지 않습니다. " + request.getId()));

        if (request.getNewPassword().equals(request.getCheckNewPassword()) == false) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        findUser.changePassword(request.getNewPassword());

        return findUser.getId();
    }
}
