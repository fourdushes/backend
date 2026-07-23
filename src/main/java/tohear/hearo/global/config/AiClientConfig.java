package tohear.hearo.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AiClientConfig {

    @Bean
    public RestClient aiRestClient() {
        return RestClient.builder()
                .baseUrl("http://43.202.195.242:5000")
                .build();
    }

}
