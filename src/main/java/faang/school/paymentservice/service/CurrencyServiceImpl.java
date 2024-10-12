package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.ExchangeRatesProperties;
import faang.school.paymentservice.dto.Rate;
import faang.school.paymentservice.dto.RedisCacheConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {
    private final ExchangeRatesProperties exchangeRatesProperties;
    private final RedisCacheConfigurationProperties redisCacheConfigurationProperties;
    private final WebClient webClient;
    private final RedisCacheManager cacheManager;

    @Override
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 3000))
    public Rate updateCurrency() {
        Mono<Rate> response = webClient.get()
                .uri(String.join("", "/latest?access_key =" + exchangeRatesProperties.getKey()
                        + "& base=" + exchangeRatesProperties.getBase()))
                .retrieve()
                .bodyToMono(Rate.class)
                .onErrorResume(Mono::error);
        log.info("rate got: " + response);
        Objects.requireNonNull(cacheManager.getCache(redisCacheConfigurationProperties.getCaches().get("current_rate")))
                .put("current_rate", Objects.requireNonNull(response));
        log.info("rate send to cache");
        return response.block();
    }
}
