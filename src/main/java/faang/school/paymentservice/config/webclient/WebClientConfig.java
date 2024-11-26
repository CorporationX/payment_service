package faang.school.paymentservice.config.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.exchange.url}")
    String url;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {

        return builder
                .baseUrl(url)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}