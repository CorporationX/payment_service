package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.payment.ExchangeRates;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final WebClient webClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${currency.exchange.access-key}")
    private String accessKey;

    @Value("${currency.exchange.actual-currency}")
    private String actualCurrency;

    @Value("${currency.exchange.base}")
    private String baseCurrency;

    @Value("${redis.key}")
    private String redisKey;

    @Retryable(retryFor = FeignException.class, maxAttempts = 10, backoff = @Backoff(delay = 1000, multiplier = 3))
    public void fetchCurrencyRates() {
        ExchangeRates exchangeRates = webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/latest")
                        .queryParam("access-key", accessKey)
                        .queryParam("symbols", actualCurrency)
                        .queryParam("base", baseCurrency)
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRates.class)
                .block();

        if (exchangeRates != null) {
            redisTemplate.opsForValue().set(redisKey, exchangeRates);
        }
    }

    public ExchangeRates getCurrencyRates() {
        return (ExchangeRates) redisTemplate.opsForHash().entries(redisKey);
    }
}
