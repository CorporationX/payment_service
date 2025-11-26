package faang.school.paymentservice.service;

import faang.school.paymentservice.client.CurrencyRateFetcher;
import faang.school.paymentservice.dto.LatestRatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRateFetcher currencyRateFetcher;
    private final ReactiveRedisTemplate<String, LatestRatesResponse> redisTemplate;

    @Value("${external.currency.redis-cache.key}")
    private String redisKey;

    @Value("${external.currency.base-currency}")
    private String baseCurrency;

    public Mono<Void> updateRates() {
        log.info("Refreshing currency rates from external API");

        return currencyRateFetcher.getLatestRates(baseCurrency)
                .flatMap(this::saveToCache)
                .doOnSuccess(success -> log.info("Currency rates successfully refreshed"))
                .doOnError(error -> log.error("Failed to refresh currency rates", error))
                .then();
    }

    public Mono<LatestRatesResponse> getAllRates() {
        return redisTemplate.opsForValue().get(redisKey)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Rates not found in Redis. Trying updateRates()");
                    return updateRates()
                            .then(redisTemplate.opsForValue().get(redisKey))
                            .switchIfEmpty(Mono.error(new IllegalStateException("Rates still not present in Redis after update")));
                }))
                .doOnError(error -> log.error("Failed to load rates from Redis", error));
    }

    public Mono<Double> getRate(String toRate) {
        return getAllRates()
                .handle((rates, sink) -> {
                    Double to = rates.getRates().get(toRate);
                    if (to == null) {
                        log.debug("We dont use rate for that currency: {}", toRate);
                        sink.error(new IllegalArgumentException("Currency " + toRate + " not found"));
                    } else {
                        log.debug("Exchange rate from {} to {} = {}", baseCurrency, toRate, to);
                        sink.next(to);
                    }

                });
    }

    private Mono<Void> saveToCache(LatestRatesResponse response) {
        return redisTemplate.opsForValue()
                .set(redisKey, response)
                .doOnSuccess(success -> log.debug("Saved latest rates to Redis"))
                .doOnError(error -> log.error("Failed to save latest rates to Redis", error))
                .then();
    }
}
