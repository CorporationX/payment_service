package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.ExchangeRateResponseDto;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final WebClient webClient;

    @Value("${exchange.url}")
    private String url;

    @Value("${exchange.access_key}")
    private String key;

    @Value("${exchange.currency}")
    private String base;

    @Value("${exchange.symbols}")
    private String symbols;

    private String getApiUrl() {
        return String.format("%s?app_id=%s&base=%s&symbols=%s", url, key, base, symbols);
    }

    public Mono<ExchangeRateResponseDto> saveExchangeRate() {
        return webClient.get()
                .uri(getApiUrl())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(HttpTimeoutException.class)
                                .flatMap(error -> Mono.error(
                                        new RuntimeException("Client error: " + response.statusCode()))))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("Server error: " + response.statusCode())))
                .bodyToMono(ExchangeRateResponseDto.class)
                .timeout(Duration.ofSeconds(5));
    }
}