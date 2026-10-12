package tohear.hearo.texttalk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.response.Result;
import tohear.hearo.texttalk.domain.TextTalkResponse;
import tohear.hearo.texttalk.service.TextTalkService;
import tohear.hearo.user.auth.principal.CurrentMedicalUser;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;

@RestController
@RequiredArgsConstructor
public class TextTalkController {

    private final TextTalkService textTalkService;

    @GetMapping("/api/show-text-talk")
    public Result showTextTalk(@CurrentMedicalUser MedicalUserPrincipal principal) {
        TextTalkResponse response = textTalkService.showTextTalk(principal);
        return new Result<>("200", "필담 화면이 정상적으로 작동합니다.", response);
    }
}
