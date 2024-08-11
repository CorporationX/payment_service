package faang.school.paymentservice.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {
    @Value("${currency.exchange.url}")
    private String BASE_URL;
    @Value("${currency.exchange.apiKey}")
    private String API_KEY;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("access_key",API_KEY)
                .build();
    }
}
