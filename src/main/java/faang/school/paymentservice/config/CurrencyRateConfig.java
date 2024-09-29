package faang.school.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Currency;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class CurrencyRateConfig {

    @Bean
    ConcurrentMap<Currency, Double> currencyRubRate() {
        return new ConcurrentHashMap<>();
    }
}
