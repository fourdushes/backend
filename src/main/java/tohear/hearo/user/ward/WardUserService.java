package tohear.hearo.user.ward;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.security.JwtTokenProvider;
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
    private final StringRedisTemplate redisTemplate;

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
        
        String accessToken = tokenProvider.createAccessToken(user.getId(), userType);
        String refreshToken = tokenProvider.createRefreshToken(user.getId(), userType);

        redisTemplate.opsForValue().set(
            "refresh-token:" + user.getId(),
            refreshToken,
            Duration.ofMillis(tokenProvider.getRefreshTokenValidityInMilliseconds())
        );
        
        return new LoginUserResponse(accessToken, user.getId(), userType, refreshToken);
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

        // 이메일 인증 여부 확인
       String verified = redisTemplate.opsForValue().get("mail-verified:" + request.getEmail());
       if (!"true".equals(verified)) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다. 이메일 인증을 먼저 진행해주세요.");
        }

        WardUser findUser = userRepository.findByEmail(request.getEmail()).orElseThrow(
            () -> new IllegalArgumentException("이메일이 올바르지 않습니다. " + request.getEmail()));

        if (!findUser.getName().equals(request.getName())) {
            throw new IllegalArgumentException("이름이 올바르지 않습니다. " + request.getName());
        }

        String token = UUID.randomUUID().toString(); // 랜덤한 토큰 생성
        String redisKey = "password-reset:" + findUser.getId(); // 혹시 모를 다른 토큰 ID와 중복 방지
        redisTemplate.opsForValue().set(redisKey, token, Duration.ofMinutes(3)); // 토큰을 Redis에 저장하고 3분 동안 유효하도록 설정

        return new ToChangePasswordResponse(findUser.getId(), UserType.WARD, token);

    }

    @Override
    @Transactional
    public String changePassword(ChangePasswordRequest request) {

        String redisKey = "password-reset:" + request.getId();
        String savedToken = (String) redisTemplate.opsForValue().get(redisKey);

        if (savedToken == null || !savedToken.equals(request.getTempToken())) {
            throw new IllegalArgumentException("인증 시간이 만료되었거나 유효하지 않은 접근입니다. 처음부터 다시 시도해주세요.");
        }

        WardUser findUser = userRepository.findById(request.getId()).orElseThrow(
            () -> new IllegalArgumentException("아이디가 올바르지 않습니다. " + request.getId()));

        if (!request.getNewPassword().equals(request.getCheckNewPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        findUser.changePassword(encodedNewPassword);

        redisTemplate.delete(redisKey);

        return findUser.getId();
    }

}
