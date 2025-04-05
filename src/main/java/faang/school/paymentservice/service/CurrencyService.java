package faang.school.paymentservice.service;

import faang.school.paymentservice.model.CurrencyResponse;
import faang.school.paymentservice.model.LatestRatesEndpoint;
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

    private final WebClient webClient;
    private final LatestRatesEndpoint endpoint;

    private CurrencyResponse currencyResponse;
    private Map<String, Double> currencyRates;

    @Retryable(
            retryFor = {Exception.class},
            maxAttemptsExpression = "#{@currencyRateRetryProperties.maxAttempts}",
            backoff = @Backoff(delayExpression = "#{@currencyRateRetryProperties.delay}")
    )
    public Mono<Void> updateCurrencyRates() {
        return webClient
                .get()
                .uri(endpoint.url() + "?access_key=" + endpoint.access_key() +
                        "&base=" + endpoint.base() + "&symbols=" + endpoint.symbols())
                .retrieve()
                .bodyToMono(CurrencyResponse.class)
                .doOnNext(response -> {
                    this.currencyResponse = response;
                    currencyRates = response.rates();
                    log.info("Курсы валют успешно обновлены: {}", currencyRates);
                    log.info("Базовая валюта: {}", currencyResponse.base());
                })
                .doOnError(throwable -> {
                    log.error("Ошибка при обновлении курсов валют: {}", throwable.getMessage());
                })
                .then();
    }
}

