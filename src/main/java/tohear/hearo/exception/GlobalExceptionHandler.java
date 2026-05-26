package tohear.hearo.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tohear.hearo.global.Result;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 프로젝트 어디서든 IllegalArgumentException이 터지면 이 메서드가 가로챕니다.
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        // e.getMessage()에는 서비스에서 넣은 "아이디/비밀번호가 올바르지 않습니다"가 들어있음
        return new Result<>("400", e.getMessage(), null);
    }
}
