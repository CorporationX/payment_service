package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.client.CurrencyRateClient;
import faang.school.paymentservice.dto.CurrencyRateResponse;
import faang.school.paymentservice.service.CurrencyService;
import faang.school.paymentservice.util.RedisSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRateClient currencyRateClient;
    private final RedisSaver redisSaver;

    @Value("${currency-rates.redis.key}")
    private String redisKey;

    @Override
    public Mono<CurrencyRateResponse> getLatestRates() {
        return currencyRateClient.fetchLatestRates()
                .doOnSuccess(response -> {
                    log.info("Successfully fetched exchange rates, caching...");
                    redisSaver.save(redisKey, response);
                })
                .doOnError(error -> log.error("Failed to fetch exchange rates: {}", error.getMessage()));
    }
}