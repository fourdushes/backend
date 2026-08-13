package tohear.hearo.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AiClientConfig {

    @Bean
    public RestClient aiRestClient(
            @Value("${AI_SERVICE_BASE_URL:http://127.0.0.1:5000}") String baseUrl
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

}
