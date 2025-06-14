package faang.school.paymentservice.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Value("${open_exchange_api.app-id}")
    private String openExchangeRatesAppId;

    @Bean
    public RequestInterceptor openExchangeRatesRequestInterceptor() {
        return template -> template.query("app_id", openExchangeRatesAppId);
    }
}
