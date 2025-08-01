package faang.school.paymentservice.config;

import faang.school.paymentservice.config.property.exchangerates.ExchangeRatesProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final ExchangeRatesProperty exchangeRatesProperty;

    @Bean
    public WebClient exchangeRatesWebClient() {
        return WebClient.builder()
                .baseUrl(exchangeRatesProperty.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
