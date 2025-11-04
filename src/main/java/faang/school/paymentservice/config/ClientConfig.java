package faang.school.paymentservice.config;

import faang.school.paymentservice.store.currencyRate.CurrencyRateStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    public CurrencyRateStore currencyRateStore() {
        return new CurrencyRateStore();
    }
}
