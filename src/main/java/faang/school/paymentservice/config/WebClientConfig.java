package faang.school.paymentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${exchange.base_url}")
    private String API_URL;

    @Bean
    public WebClient webClient(WebClient.Builder builder){
        return builder
                .baseUrl(API_URL)
                .build();
    }
}
