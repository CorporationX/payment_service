package faang.school.paymentservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.service.CurrencyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class CurrencyServiceConfig {

    @Value("${redis.channel.exchange_rates}")
    private String redisKey;

    @Bean
    public CurrencyService currencyService(
            ExchangeRatesClient exchangeRatesClient,
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper) {
        return new CurrencyService(exchangeRatesClient, redisTemplate, objectMapper, redisKey);
    }
}
