package faang.school.paymentservice.service;

import faang.school.paymentservice.model.CurrencyResponse;
import faang.school.paymentservice.model.LatestRatesEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;


@Service
@RequiredArgsConstructor
@EnableConfigurationProperties({LatestRatesEndpoint.class})
public class CurrencyService {

private final WebClient webClient;
private final LatestRatesEndpoint latestRatesEndpoint;

    private Map<String, Double> currencyRates; // Хранение курсов валют в памяти

    @Retryable(value = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public Mono<CurrencyResponse> updateCurrencyRates() {
            return webClient
                    .get()
                    .uri(String.join("", "/users/", id))
                    .retrieve()
                    .bodyToMono(CurrencyResponse.class);


    }
}
