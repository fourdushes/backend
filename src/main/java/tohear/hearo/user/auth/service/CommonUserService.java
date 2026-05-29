package tohear.hearo.user.auth.service;

import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.auth.dto.request.IdFindRequest;
import tohear.hearo.user.auth.dto.request.JoinUserRequest;

public interface CommonUserService {

    boolean supports(UserType userType);
    String join(JoinUserRequest request);
    String findId(IdFindRequest request);

}
