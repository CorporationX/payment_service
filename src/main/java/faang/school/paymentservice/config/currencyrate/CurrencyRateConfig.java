package faang.school.paymentservice.config.currencyrate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CurrencyRateConfig {

    @Value("${currency-rate-fetcher.url}")
    private String url;

    @Bean
    public WebClient currencyRateWebClient() {
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }

}
