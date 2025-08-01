package faang.school.paymentservice.client.currency;

import faang.school.paymentservice.config.property.exchangerates.ExchangeRatesProperty;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRateDto;
import faang.school.paymentservice.exception.ApiRequestException;
import faang.school.paymentservice.exception.EmptyApiResponseException;
import faang.school.paymentservice.exception.ExternalApiException;
import faang.school.paymentservice.exception.InvalidApiResponseException;
import faang.school.paymentservice.exception.RetryExhaustedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.net.URI;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRatesApi implements CurrencyClient {
    private final WebClient exchangeRatesWebClient;
    private final ExchangeRatesProperty property;

    @Override
    public Mono<CurrencyRateDto> getCurrencyRates() {
        log.info("Getting actual currency rates for {}", property.baseCurrency());

        return exchangeRatesWebClient.get()
                .uri(this::buildUri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::processHttpErrorCodeResponse)
                .onStatus(HttpStatusCode::is5xxServerError, this::processHttpErrorCodeResponse)
                .bodyToMono(CurrencyRateDto.class)
                .switchIfEmpty(Mono.error(new EmptyApiResponseException("Empty response body from API")))
                .flatMap(this::processResponse)
                .retryWhen(prepareBehaviorRetry());
    }

    private Mono<? extends Throwable> processHttpErrorCodeResponse(ClientResponse response) {
        String endpoint = property.uri().latest();
        HttpStatusCode status = response.statusCode();
        log.info("Processing http error code {} on endpoint {}", status, endpoint);
        String message = String.format("API error %s at endpoint %s", status, endpoint);

        if (status.is4xxClientError()) {
            return Mono.error(new ApiRequestException(message));
        }

        return Mono.error(new ExternalApiException(message));
    }

    private Mono<CurrencyRateDto> processResponse(CurrencyRateDto response) {
        log.info("Processing response from API: {}", response);
        if (!response.success()) {
            if (response.error() == null) {
                return Mono.error(new InvalidApiResponseException("Field 'success' is false, but error is null"));
            }

            return Mono.error(new ApiRequestException(
                    "Request error {} : {}", response.error().code(), response.error().info()));
        }

        return Mono.just(response);
    }

    private URI buildUri(UriBuilder uriBuilder) {
        String currencies = Currency.getCurrenciesAsString();
        log.info("Currency list: {}", currencies);
        return uriBuilder
                .path(property.uri().latest())
                .queryParam("access_key", property.accessKey())
                .queryParam("base", property.baseCurrency())
                .queryParam("symbols", currencies)
                .build();
    }

    private Retry prepareBehaviorRetry() {
        return Retry.backoff(property.retry().maxAttempts(),
                             Duration.of(property.retry().delay(),
                                         property.retry().delayTimeUnit()))
                .jitter(property.retry().jitter())
                .filter(throwable -> throwable instanceof ExternalApiException)
                .doBeforeRetry(retrySignal -> log.info(
                        "Attempt #{}, cause: {}", (retrySignal.totalRetries() + 1), retrySignal.failure().getMessage()))
                .onRetryExhaustedThrow((spec, signal) ->
                                               new RetryExhaustedException("Retries exhausted: {}/{}",
                                                                           signal.failure(),
                                                                           property.retry().maxAttempts(),
                                                                           signal.totalRetries()));
    }
}
