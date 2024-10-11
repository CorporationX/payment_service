package faang.school.paymentservice.service.exchangerate;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class FetcherRatesFromApi {

    private final WebClient webClient;

    @Value("${currency.rate.api-key}")
    private String apiKey;

    @Value(("${currency.rate.base}"))
    private String baseCurrency;

    @Value("${currency.rate.symbols}")
    private String symbolsCurrency;


    public Mono<Map<String, Double>> fetchRatesFromApi() {
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
                .map(ApiResponse::getRates)
                .map(rates -> {
                    if (rates == null) {
                        log.error("Rates map is null");
                        return Collections.emptyMap();
                    }
                    return rates;
                });
    }
}
