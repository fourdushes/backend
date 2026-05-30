package tohear.hearo.user.auth;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.JwtTokenProvider;
import tohear.hearo.global.Result;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final List<UserService> userServices;
    private final JwtTokenProvider tokenProvider;
    private final CommonUserService commonUserService;

    @PostMapping("/join") // 회원 가입, 사용자 유형에 따라 다른 서비스 호출
    public Result join(@RequestBody JoinUserRequest request) {

        UserService userService = userServices.stream()
                .filter(service -> service.supports(request.getUserType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 유형입니다."));
        
        String userId = userService.join(request);
        return new Result<>("200", "회원 가입이 완료되었습니다.", userId);
    }

    @PostMapping("/login") // 로그인, 사용자 유형에 따라 다른 서비스 호출
    public Result login(@RequestBody LoginUserRequest request) {

        UserType userType = commonUserService.checkUserTypeById(request.getId());

        UserService userService = userServices.stream()
                .filter(service -> service.supports(userType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 유형입니다."));

        LoginUserResponse response = userService.validateLogin(request);
        return new Result<>("200", "로그인이 성공했습니다.", response);

    }

    @PostMapping("/find-id") // 아이디 찾기, 이메일로 사용자 유형 확인 후 해당 서비스에서 아이디 찾기
    public Result findId(@RequestBody IdFindRequest request) {

        UserType userType = commonUserService.checkUserTypeByEmail(request.getEmail());
        
        UserService userService = userServices.stream()
            .filter(service -> service.supports(userType))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 유형입니다."));
            
        String findUserId = userService.findId(request);
        return new Result<>("200", "ID 찾기가 성공했습니다.", findUserId);

    }

    @PostMapping("/to-change-password")
    public Result toChangePassword(@RequestBody ToChangePasswordRequest request) {

        UserType userType = commonUserService.checkUserTypeByEmail(request.getEmail());

        UserService userService = userServices.stream()
            .filter(service -> service.supports(userType))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 유형입니다."));

        // 아이디랑 이메일 검증
        ToChangePasswordResponse response = userService.validateToChangePassword(request);
        
        return new Result<>("200", "인증에 성공했습니다.", response);
    }

    @PostMapping("/change-password")
    public Result changePassword(@RequestBody ChangePasswordRequest request) {

        UserService userService = userServices.stream()
            .filter(service -> service.supports(request.getUserType()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 유형입니다."));

        // 비밀번호 변경
        String userId = userService.changePassword(request);

        return new Result<>("200", "비밀번호 변경에 성공했습니다.", userId);
    }


}
