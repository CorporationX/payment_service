package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.ExchangeRatesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeRates {

    private final WebClient webClient;

    @Value("${services.exchangerates.appId}")
    private String appId;

    public Mono<ExchangeRatesDto> fetchData() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest")
                        .queryParam("access_key", appId)
                        .build())
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("Authorization error: " + response.statusCode()))
                )
                .bodyToMono(ExchangeRatesDto.class)
                .onErrorResume(e -> {
                    log.error(e.getMessage(), e);
                    return Mono.error(e);
                })
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                .onErrorReturn(new ExchangeRatesDto());
    }
}
