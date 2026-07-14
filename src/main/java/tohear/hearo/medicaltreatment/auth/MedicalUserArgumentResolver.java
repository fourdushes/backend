package tohear.hearo.medicaltreatment.auth;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.JwtTokenProvider;

@Component
@RequiredArgsConstructor
public class MedicalUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentMedicalUser.class)
                && parameter.getParameterType().equals(MedicalUserPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String authorization = webRequest.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("인증 토큰이 필요합니다.");
        }

        String token = authorization.substring(7);
        try {
            return new MedicalUserPrincipal(tokenProvider.getUserId(token), tokenProvider.getUserType(token));
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 인증 토큰입니다.");
        }
    }
}
