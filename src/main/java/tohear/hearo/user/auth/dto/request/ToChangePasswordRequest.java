package tohear.hearo.user.auth.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ToChangePasswordRequest {

    private String name;
    private String email;

    public ToChangePasswordRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

}
