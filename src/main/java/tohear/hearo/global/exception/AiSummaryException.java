package tohear.hearo.global.exception;

public class AiSummaryException extends RuntimeException {
    public AiSummaryException(String message) {
        super(message);
    }

    public AiSummaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
