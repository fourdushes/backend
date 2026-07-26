package tohear.hearo.global.validation;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import tohear.hearo.medicaltreatment.chat.dto.request.SendTextMessageRequest;
import tohear.hearo.user.auth.dto.request.ChangePasswordRequest;
import tohear.hearo.user.auth.dto.request.EmailCheckNumberDto;
import tohear.hearo.user.auth.dto.request.EmailRequestDto;
import tohear.hearo.user.auth.dto.request.IdFindRequest;
import tohear.hearo.user.auth.dto.request.JoinUserRequest;
import tohear.hearo.user.auth.dto.request.LoginUserRequest;
import tohear.hearo.user.auth.dto.request.ToChangePasswordRequest;
import tohear.hearo.user.auth.dto.request.TokenReissueRequest;

class RequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void rejectsBlankJoinFieldsAndInvalidEmail() {
        JoinUserRequest request = new JoinUserRequest();
        request.setId(" ");
        request.setName("");
        request.setEmail("invalid-email");
        request.setPassword(null);
        request.setUserType(null);

        assertThat(validator.validate(request)).hasSize(6);
    }

    @Test
    void rejectsJoinIdShorterThanFiveCharacters() {
        JoinUserRequest request = new JoinUserRequest();
        request.setId("abcd");
        request.setName("사용자");
        request.setEmail("user@test.com");
        request.setPassword("password");
        request.setUserType(tohear.hearo.user.auth.domain.UserType.WARD);

        assertThat(validator.validate(request))
            .singleElement()
            .satisfies(violation ->
                assertThat(violation.getMessage())
                    .isEqualTo("아이디는 5자 이상이어야 합니다."));
    }

    @Test
    void rejectsBlankLoginFields() {
        LoginUserRequest request = new LoginUserRequest();
        request.setId(" ");
        request.setPassword("");

        assertThat(validator.validate(request)).hasSize(2);
    }

    @Test
    void rejectsInvalidFindIdAndPasswordVerificationFields() {
        IdFindRequest findIdRequest = new IdFindRequest();
        findIdRequest.setName("");
        findIdRequest.setEmail("invalid");
        ToChangePasswordRequest passwordRequest = new ToChangePasswordRequest();
        passwordRequest.setName(" ");
        passwordRequest.setEmail("invalid");

        assertThat(validator.validate(findIdRequest)).hasSize(2);
        assertThat(validator.validate(passwordRequest)).hasSize(2);
    }

    @Test
    void rejectsBlankPasswordChangeFields() {
        ChangePasswordRequest request = new ChangePasswordRequest();

        assertThat(validator.validate(request)).hasSize(5);
    }

    @Test
    void rejectsInvalidMailVerificationFields() {
        EmailRequestDto sendRequest = new EmailRequestDto();
        sendRequest.setEmail("invalid");
        EmailCheckNumberDto checkRequest = new EmailCheckNumberDto();
        checkRequest.setEmail("invalid");
        checkRequest.setCheckNumber(" ");

        assertThat(validator.validate(sendRequest)).hasSize(1);
        assertThat(validator.validate(checkRequest)).hasSize(2);
    }

    @Test
    void rejectsBlankRefreshTokenAndChatMessage() {
        TokenReissueRequest tokenRequest = new TokenReissueRequest();
        tokenRequest.setRefreshToken(" ");
        SendTextMessageRequest messageRequest = new SendTextMessageRequest();
        messageRequest.setContent("");

        assertThat(validator.validate(tokenRequest)).hasSize(1);
        assertThat(validator.validate(messageRequest)).hasSize(1);
    }
}
