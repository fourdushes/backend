package tohear.hearo.medicaltreatment.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tohear.hearo.user.auth.domain.UserType;

@Getter
@AllArgsConstructor
public class MedicalUserPrincipal {

    private final String userId;
    private final UserType userType;
}
