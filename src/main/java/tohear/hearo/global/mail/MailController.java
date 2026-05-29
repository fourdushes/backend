package tohear.hearo.global.mail;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.Result;
import tohear.hearo.global.dto.EmailCheckNumberDto;
import tohear.hearo.global.dto.EmailRequestDto;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public Result<?> sendMail(@RequestBody EmailRequestDto request) {
        mailService.sendMail(request.getEmail());

        return new Result<>("200", "인증번호가 발송되었습니다.", null);
    }

    @PostMapping("/check")
    public Result<?> checkMail(@RequestBody EmailCheckNumberDto request) {
        boolean isCorrect = mailService.checkCode(request.getEmail(), request.getCheckNumber());
        if (isCorrect) {
            return new Result<>("200", "인증번호가 일치합니다.", null);
        }
        return new Result<>("400", "인증번호가 일치하지 않습니다.", null);
    }

}
