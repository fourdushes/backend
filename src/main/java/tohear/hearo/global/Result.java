package tohear.hearo.global;

import lombok.Getter;

@Getter
public class Result<T> {

    private String status;
    private String message;
    private T data;
    
    public Result(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }


}
