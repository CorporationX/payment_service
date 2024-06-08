package faang.school.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CurrencyServiceConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .build();
    }
}
