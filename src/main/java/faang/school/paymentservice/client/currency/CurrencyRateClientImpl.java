package faang.school.paymentservice.client.currency;

import faang.school.paymentservice.client.CurrencyRateClient;
import faang.school.paymentservice.dto.CurrencyRateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@Slf4j
@RequiredArgsConstructor
public class CurrencyRateClientImpl implements CurrencyRateClient {
    private final WebClient webClient;
    private final Retry retry;
    @Value("${currency-rates.api.access-key}")
    private String accessKey;

    @Override
    public Mono<CurrencyRateResponse> fetchLatestRates() {
        log.info("Fetching latest exchange rates from external API");

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("access_key", accessKey)
                        .build())
                .retrieve()
                .bodyToMono(CurrencyRateResponse.class)
                .retryWhen(retry)
                .doOnSuccess(response -> log.info("Done"))
                .doOnError(error -> log.error("Failed to fetch exchange rates: {}", error.getMessage()));
    }
}