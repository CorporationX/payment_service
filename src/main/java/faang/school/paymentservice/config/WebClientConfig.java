package faang.school.paymentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${external.currency.api.url}")
    private String apiUrl;

    @Bean(name = "currencyWebClient")
    public WebClient currencyWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(apiUrl)
                .build();
    }
}
