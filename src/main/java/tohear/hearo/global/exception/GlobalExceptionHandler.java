package tohear.hearo.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tohear.hearo.global.Result;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 프로젝트 어디서든 IllegalArgumentException이 터지면 이 메서드가 가로챕니다.
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        // e.getMessage()에는 서비스에서 넣은 "아이디/비밀번호가 올바르지 않습니다"가 들어있음
        return new Result<>("400", e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public Result<?> handleIllegalStateException(IllegalStateException e) {
        // 이제 500으로 안 떨어지고, 준형님이 만든 이쁜 Result 포맷으로 나갑니다!
        return new Result<>("400", e.getMessage(), null); 
    }
}
