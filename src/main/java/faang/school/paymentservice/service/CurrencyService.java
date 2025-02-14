package faang.school.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.payment.ExchangeRates;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final ExchangeRatesClient exchangeRatesClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${currency.exchange.access-key}")
    private String accessKey;

    @Value("${currency.exchange.actual-currency}")
    private String actualCurrency;

    @Value("${redis.channels.calculations-channel.name}")
    private String redisKey;

    @Retryable(retryFor = FeignException.class, maxAttempts = 10, backoff = @Backoff(delay = 1000, multiplier = 3))
    public void fetchCurrencyRates() {
        try {
            ExchangeRates exchangeRates = exchangeRatesClient.getExchangeRates(accessKey, actualCurrency);
            if (exchangeRates != null) {
                redisTemplate.opsForValue().set(redisKey, exchangeRates);
            }
        } catch (FeignException e) {
            log.error("Ошибка при получении информации о курсах валют", e);
        }
    }

    public ExchangeRates getCurrencyRates() {
        return Optional.ofNullable(redisTemplate.opsForValue().get(redisKey))
                .map(cachedRates -> objectMapper.convertValue(cachedRates, ExchangeRates.class))
                .orElseGet(() -> {
                    fetchCurrencyRates();
                    return objectMapper.convertValue(redisTemplate
                            .opsForValue()
                            .get(redisKey), ExchangeRates.class);
                });
    }
}
