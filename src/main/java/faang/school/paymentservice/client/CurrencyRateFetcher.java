package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.LatestRatesResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final WebClient currencyWebClient;

    @Value("${external.currency.api.timeout-seconds:5}")
    private long timeoutSeconds;

    @Value("${external.currency.api.backoff-delay-millis}")
    private long backoffDelay;

    @Value("${external.currency.api.maxAttempts}")
    private int maxAttempts;

    @Value("${external.currency.api.key}")
    private String apiKey;

    @Value("${external.currency.base-currency}")
    private String baseCurrency;

    public Mono<LatestRatesResponse> getLatestRates() {
        log.debug("Requesting latest currency rates, base = {}", baseCurrency);
        return currencyWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/latest")
                        .queryParam("base", baseCurrency)
                        .queryParam("access_key", apiKey)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> {
                            log.error("Currency API returned error: {}", clientResponse.statusCode());
                            return Mono.error(new IllegalStateException(
                                    "Currency API returned error: " + clientResponse.statusCode()));
                        })
                .bodyToMono(LatestRatesResponse.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .switchIfEmpty(Mono.error(new IllegalStateException("Empty response from currency API")))
                .map(response -> {
                    validate(response);
                    return response;
                })
                .retryWhen(
                        reactor.util.retry.Retry
                                .backoff(maxAttempts, Duration.ofMillis(backoffDelay))
                                .filter(this::isTemporaryError)
                                .doBeforeRetry(retrySignal ->
                                        log.warn("Retry attempt {} due to temporary error: {}",
                                                retrySignal.totalRetries() + 1,
                                                retrySignal.failure().getMessage()))
                )
                .doOnError(error -> log.error("Failed to fetch currency rates", error));
    }

    private void validate(LatestRatesResponse response) {
        if (!response.isSuccess()) {
            log.error("Unsuccessful response from currency API");
            throw new IllegalStateException("Currency API returned unsuccessful response");
        }

        if (response.getBase() == null || response.getBase().isEmpty()) {
            log.error("Invalid response from currency API");
            throw new IllegalArgumentException("Currency API returned null base");
        }

        if (!response.getBase().equals(baseCurrency)) {
            log.error("Mismatched base currency in response: expected {}, got {}", baseCurrency, response.getBase());
            throw new IllegalArgumentException("Mismatched base currency in response");
        }

        if (response.getRates() == null || response.getRates().isEmpty()) {
            log.error("Invalid response from currency API");
            throw new IllegalStateException("Invalid response from currency API");
        }
    }

    private boolean isTemporaryError(Throwable error) {
        return error instanceof WebClientResponseException exception
                && exception.getStatusCode().is5xxServerError();
    }
}
