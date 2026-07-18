package tohear.hearo.user.auth.principal;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MedicalTreatmentWebConfig implements WebMvcConfigurer {

    private final MedicalUserArgumentResolver medicalUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(medicalUserArgumentResolver);
    }
}
