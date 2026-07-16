package tohear.hearo.user.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.response.Result;
import tohear.hearo.user.auth.dto.request.TokenReissueRequest;
import tohear.hearo.user.auth.dto.response.TokenReissueResponse;
import tohear.hearo.user.auth.service.TokenReissueService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class TokenReissueController {

    private final TokenReissueService tokenReissueService;

    @PostMapping("/token/reissue")
    public Result reissueToken(@RequestBody TokenReissueRequest request) {
        TokenReissueResponse response = tokenReissueService.reissue(request);

        return new Result<>(
            "200",
            "토큰 재발급에 성공했습니다.",
            response
        );
    }

}
