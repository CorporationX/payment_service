package faang.school.paymentservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import faang.school.paymentservice.dto.Currency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    @Value("${currency-api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final Map<String, Double> currencyRates = new ConcurrentHashMap<>();
    String currencies = Arrays.stream(Currency.values())
            .map(Enum::name)
            .collect(Collectors.joining(", "));

    @Retryable(retryFor = {WebClientResponseException.class, ResourceAccessException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000, maxDelay = 2))
    public void fetchAndStoreCurrencyRates() {
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("latest")
                        .queryParam("access_key", apiKey)
                        .queryParam("symbols", currencies)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Ошибка сервера: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Server error"));
                })
                .bodyToMono(JsonNode.class)
                .subscribe(response -> {
                    if (response != null && response.has("rates")) {
                        JsonNode ratesNode = response.get("rates");
                        currencyRates.clear();
                        ratesNode.fields().forEachRemaining(entry -> {
                            currencyRates.put(entry.getKey(), entry.getValue().asDouble());
                        });
                        log.info("Курсы валют обновлены: {}", currencyRates);
                    }
                }, error -> {
                    log.error("Ошибка при получении курсов валют: {}", error.getMessage());
                });
    }
}

