package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Rate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class CurrencyService {
    @Value("${exchange_rates.key}")
    private String key;
    @Value("${exchange_rates.base}")
    private String base;
    @Value("${spring.cache.redis.caches.current_rate}")
    private String cacheName;

    private final WebClient webClient;
    private final RedisCacheManager cacheManager;


    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 3000))
    public void updateCurrency() {
        Mono<Rate> response = webClient.get()
                .uri(String.join("", "/latest?access_key =" + key + "& base=" + base))
                .retrieve()
                .bodyToMono(Rate.class);
        log.info("rate got: " + response);
        Objects.requireNonNull(cacheManager.getCache(cacheName)).put("current_rate", response);
        log.info("rate send to cache");
    }
}
