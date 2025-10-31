package faang.school.paymentservice.service.currencyrate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyApiClient {

    private final WebClient webClient;

    @Value("${currency.base-currency:RUR}")
    private String baseCurrency;

    @Value("${currency.api-key}")
    private String apiKey;

    @Retryable
            (retryFor = {WebClientResponseException.class, RuntimeException.class},
             maxAttemptsExpression = "${retry.max-attempts}",
             backoff = @Backoff(delayExpression = "${retry.max-backoff}"))
    public CurrencyRateDto fetchLatestRates() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest")
                        .queryParam("access_key", apiKey)
                        .queryParam("base", baseCurrency)
                        .build())
                .retrieve()
                .bodyToMono(CurrencyRateDto.class)
                .timeout(Duration.ofSeconds(10))
                .doOnSuccess(rates -> log.info("Successfully fetched rates for date: {}", rates.date()))
                .doOnError(error -> log.error("Error fetching currency rates: {}", error.getMessage()))
                .block();
    }
}