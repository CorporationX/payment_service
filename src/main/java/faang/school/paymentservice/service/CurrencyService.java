package faang.school.paymentservice.service;

import faang.school.paymentservice.model.CurrencyResponse;
import faang.school.paymentservice.model.LatestRatesEndpoint;
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
@EnableConfigurationProperties({LatestRatesEndpoint.class})
public class CurrencyService {

    private final WebClient webClient;
    private final LatestRatesEndpoint endpoint;

    private CurrencyResponse currencyResponse;
    private Map<String, Double> currencyRates;

    @Retryable(value = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public Mono<Void> updateCurrencyRates() {
        return webClient
                .get()
                .uri(endpoint.url() + "?access_key=" + endpoint.access_key() +
                        "&base=" + endpoint.base() + "&symbols=" + endpoint.symbols())
                .retrieve()
                .bodyToMono(CurrencyResponse.class)
                .doOnNext(response -> {
                    this.currencyResponse = response;
                    currencyRates = response.getRates();
                    log.info("Курсы валют успешно обновлены: {}", currencyRates);
                    log.info("Базовая валюта: {}", currencyResponse.getBase());
                })
                .doOnError(throwable -> {
                    log.error("Ошибка при обновлении курсов валют: {}", throwable.getMessage());
                })
                .then(); // Возвращаем Mono<Void> для указания завершения
    }

    public CurrencyResponse getCurrencyResponse() {
        return currencyResponse;
    }

    public Map<String, Double> getCurrencyRates() {
        return currencyRates;
    }
}

