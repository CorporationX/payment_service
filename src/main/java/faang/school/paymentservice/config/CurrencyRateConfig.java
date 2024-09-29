package faang.school.paymentservice.config;

import faang.school.paymentservice.dto.Currency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class CurrencyRateConfig {
    @Value("${currency-rate-fetcher.url}")
    private String url;

    @Bean
    public ConcurrentMap<Currency, Double> currencyUsdRate() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }
}
