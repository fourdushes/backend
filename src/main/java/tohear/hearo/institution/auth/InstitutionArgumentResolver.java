package tohear.hearo.institution.auth;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.dto.AccountRole;
import tohear.hearo.global.exception.AuthenticationException;
import tohear.hearo.global.security.JwtTokenProvider;

@Component
@RequiredArgsConstructor
public class InstitutionArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentInstitution.class)
                && parameter.getParameterType().equals(InstitutionPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String authorization = webRequest.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new AuthenticationException("인증 토큰이 필요합니다.");
        }

        String token = authorization.substring(7);

        try {
            tokenProvider.validateAccessToken(token);

            if (tokenProvider.getAccountRole(token) != AccountRole.INSTITUTION) {
                throw new AuthenticationException("기관 계정만 접근할 수 있습니다.");
            }

            Long institutionId = Long.valueOf(tokenProvider.getUserId(token));
            return new InstitutionPrincipal(institutionId);
        } catch (AuthenticationException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new AuthenticationException("유효하지 않거나 만료된 인증 토큰입니다.");
        }
    }
}
