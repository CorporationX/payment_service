package faang.school.paymentservice.service;

import faang.school.paymentservice.components.CurrencyApiClient;
import faang.school.paymentservice.dto.CurrencyResponse;
import faang.school.paymentservice.properties.LatestRatesEndpoint;
import faang.school.paymentservice.properties.CurrencyRateRetryProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties({LatestRatesEndpoint.class, CurrencyRateRetryProperties.class})
public class CurrencyService {
    private final CurrencyApiClient currencyApiClient;

    @Retryable(
            retryFor = {Exception.class},
            maxAttemptsExpression = "#{@currencyRateRetryProperties.maxAttempts}",
            backoff = @Backoff(delayExpression = "#{@currencyRateRetryProperties.delay}")
    )
    public Mono<Void> updateCurrencyRates() {
        return currencyApiClient.getCurrencyRates()
                .doOnNext(response -> {
                    log.info("Курсы валют успешно обновлены: {}", response.rates());
                })
                .doOnError(throwable -> {
                    log.error("Ошибка при обновлении курсов валют: {}", throwable.getMessage());
                })
                .then();
    }
}

