package faang.school.paymentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CurrencyConfig {

    @Value("${currency.exchange.url}")
    private String url;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }
}
