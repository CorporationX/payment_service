package faang.school.paymentservice.client.api;

import faang.school.paymentservice.exception.api.APIConversionRatesException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class CurrencyApiClient {
    private final String currencyAPIUrl;
    private final WebClient.Builder webClientBuilder;


    @Retryable(retryFor = {APIConversionRatesException.class},
            maxAttemptsExpression = "${payment.exchange-rates.api.scheduler.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${payment.exchange-rates.api.scheduler.retry.backoff.delay}",
                    multiplierExpression = "${payment.exchange-rates.api.scheduler.retry.backoff.multiplier}"))
    public Mono<Map<String, Object>> updateCurrencyRates() {
        return webClientBuilder.build().get()
                .uri(currencyAPIUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .doOnError(error -> {
                    log.error("Error fetching currency rates: {}", error.getMessage());
                    throw new APIConversionRatesException("Failed to fetch currency rates", error);
                });
    }

    @Recover
    private void recover(APIConversionRatesException ex) {
        log.error("ConversionRates API call failed.\n" +
                "All retry attempts failed: {}", ex.getMessage());
    }
}
