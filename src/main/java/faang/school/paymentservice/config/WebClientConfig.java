package faang.school.paymentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${exchange_rates_api.base_url}")
    private String baseUrl;

    @Bean
    public WebClient currencyWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(baseUrl).build();
    }
}
