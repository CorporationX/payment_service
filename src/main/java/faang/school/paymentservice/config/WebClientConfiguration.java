package faang.school.paymentservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {
    private final ExchangeRatesProperties exchangeRatesProperties;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(exchangeRatesProperties.getBaseUrl())
                .build();
    }
}
