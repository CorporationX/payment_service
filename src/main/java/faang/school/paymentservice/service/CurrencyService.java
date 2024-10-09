package faang.school.paymentservice.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class CurrencyService {

    private final WebClient webClient;
    private final Map<String, Double> currencyRates = new ConcurrentHashMap<>();

    @Value("${currency.rate.api-key}")
    private String apiKey;

    @Value(("${currency.rate.base}"))
    private String baseCurrency;

    @Value("${currency.rate.symbols}")
    private String symbolsCurrency;

    public void updateCurrencyRates() {
        fetchRatesFromApi().subscribe(currencyRates::putAll);
    }

    public Double getRate(String currency) {
        return currencyRates.get(currency);
    }

    private Mono<Map<String, Double>> fetchRatesFromApi() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("access_key", apiKey)
                        .queryParam("base", baseCurrency)
                        .queryParam("symbols", symbolsCurrency)
                        .build())
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .doOnError(error -> log.error("An error has occurred {}", error.getMessage()))
                .onErrorResume(error -> {
                    log.error("An error has occurred {}", error.getMessage());
                    return Mono.just(new ApiResponse());
                })
                .map(ApiResponse::getRates)
                .map(rates -> {
                    if (rates == null) {
                        log.error("Rates map is null");
                        return Collections.emptyMap();
                    }
                    return rates;
                });
    }

    @Setter
    @Getter
    private static class ApiResponse {
        private Map<String, Double> rates;
    }
}
