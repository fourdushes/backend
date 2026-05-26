package tohear.hearo.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.Result;
import tohear.hearo.user.domain.GuardUser;
import tohear.hearo.user.domain.InstitutionsUser;
import tohear.hearo.user.domain.User;
import tohear.hearo.user.domain.UserType;
import tohear.hearo.user.domain.dto.request.JoinUserRequest;
import tohear.hearo.user.domain.dto.request.LoginUserRequest;
import tohear.hearo.user.domain.dto.response.LoginUserResponse;
import tohear.hearo.user.service.GuardUserService;
import tohear.hearo.user.service.InstitutionsUserService;
import tohear.hearo.user.service.UserService;

@RestController("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GuardUserService guardUserService;
    private final InstitutionsUserService institutionsUserService;

    @PostMapping("/join") // 회원 가입, 사용자 유형에 따라 다른 서비스 호출
    public Result join(@RequestBody JoinUserRequest request) {
        
        if (request.getUserType() == UserType.WARD) {
            User user = new User(request.getId(), request.getName(), request.getEmail(), request.getPassword(), request.getUserType());
            String id = userService.join(user);
            return new Result<>("200", "회원 가입이 완료되었습니다.", id);

        } else if (request.getUserType() == UserType.GUARDIAN) {
            GuardUser guardUser = new GuardUser(request.getId(), request.getName(), request.getEmail(), request.getPassword(), request.getUserType());
            String id = guardUserService.join(guardUser);
            return new Result<>("200", "회원 가입이 완료되었습니다.", id);

        } else if (request.getUserType() == UserType.INSTITUTIONS) {
            InstitutionsUser institutionsUser = new InstitutionsUser(request.getId(), request.getName(), request.getEmail(), request.getPassword(), request.getUserType());
            String id = institutionsUserService.join(institutionsUser);
            return new Result<>("200", "회원 가입이 완료되었습니다.", id);
        } else {
            return new Result<>("400", "유효하지 않은 사용자 유형입니다.", null);
        }
        
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginUserRequest request) {

        userService.validateLogin(request.getId(), request.getPassword());
        LoginUserResponse response = new LoginUserResponse("dummyAccessToken", request.getId());

        // 로그인 로직 구현
        return new Result<>("200", "로그인에 성공하였습니다.", response);
    }

}
