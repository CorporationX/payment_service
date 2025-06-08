package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.ExchangeRatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyService {
    private final WebClient webClient;
    @Value("${currency.rates.base}")
    private String baseCurrency;
    @Value("${exchange_rates_api.key}")
    private String apikey;

    @Retryable(
            retryFor = {WebClientRequestException.class, TimeoutException.class, RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public Map<String, Double> fetchLatestRates() {
        log.info("Fetching latest rates for currency (base = {})", baseCurrency);
        ExchangeRatesResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest")
                        .queryParam("base", baseCurrency)
                        .queryParam("access_key", apikey)
                        .build()
                )
                .retrieve()
                .bodyToMono(ExchangeRatesResponse.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorMap( throwable -> {
                    if (throwable instanceof java.util.concurrent.TimeoutException) {
                        log.error("Timeout while fetching {}", throwable.getMessage());
                        return new TimeoutException(throwable.getMessage());
                    }
                    return throwable;
                })
                .block();

        if (response == null) {
            throw new RuntimeException("Could not fetch latest rates");
        }

        if (!response.isSuccess()){
            throw new RuntimeException("Could not fetch latest rates");
        }

        log.info("Fetched rates (date={}):{}", response.getDate(), response.getRates());
        return response.getRates();
    }

    @Recover
    public Map<String, Double> recoverAfterFail(Exception e) {
        log.error("Could not get rates after retries {}", e.getMessage());
        return Map.of();
    }
}
