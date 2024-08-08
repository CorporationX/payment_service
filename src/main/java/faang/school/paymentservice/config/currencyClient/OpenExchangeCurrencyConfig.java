package faang.school.paymentservice.config.currencyClient;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenExchangeCurrencyConfig {
    @Value("${app.currency.base}")
    private String baseCurrency;
    @Value("${services.currencyClient.appId}")
    private String appId;

    @Bean
    RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.query("app_id", appId);
            requestTemplate.query("base", baseCurrency);
        };
    }
}
