package faang.school.paymentservice.config;

import faang.school.paymentservice.client.CurrencyClient;
import faang.school.paymentservice.service.CurrencyConverterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyConfig {
    @Value("${currency.app-id}")
    private String appId;

    @Bean
    public CurrencyConverterService currencyConverterService(CurrencyClient client) {
        return new CurrencyConverterService(client, appId);
    }
}
