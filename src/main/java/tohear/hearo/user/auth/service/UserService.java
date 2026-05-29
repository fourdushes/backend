package tohear.hearo.user.auth.service;

import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.auth.dto.request.ChangePasswordRequest;
import tohear.hearo.user.auth.dto.request.IdFindRequest;
import tohear.hearo.user.auth.dto.request.JoinUserRequest;
import tohear.hearo.user.auth.dto.request.LoginUserRequest;
import tohear.hearo.user.auth.dto.request.ToChangePasswordRequest;
import tohear.hearo.user.auth.dto.response.LoginUserResponse;
import tohear.hearo.user.auth.dto.response.ToChangePasswordResponse;

public interface UserService {

    boolean supports(UserType userType);
    String join(JoinUserRequest request);
    String findId(IdFindRequest request);
    LoginUserResponse validateLogin(LoginUserRequest request);
    void validateDuplicateUser(String id);
    ToChangePasswordResponse validateToChangePassword(ToChangePasswordRequest request);
    String changePassword(ChangePasswordRequest request);


}
