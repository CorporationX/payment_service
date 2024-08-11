package faang.school.paymentservice.service.rates;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.client.config.WebConfig;
import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final ExchangeRatesClient exchangeRatesClient;
    @Value("${currency.exchange.apiKey}")
    private String API_KEY;
    @Value("${currency.exchange.url}")
    private String BASE_URL;
    private final WebClient webClient;


    @Retryable(retryFor = FeignException.class, maxAttempts = 10, backoff = @Backoff(delay = 1000, multiplier = 3))
    public Map<String, Double> getActualRates() {
        Map<String, Double> rates = new HashMap<>();
        try {
            ExchangeRatesResponse response = exchangeRatesClient.getRates(API_KEY);
            if (response != null && response.getRates() != null) {
                rates.putAll(response.getRates());
            }
        } catch (FeignException e) {
            log.error("Failed to fetch rates from external API", e);
        }
        return rates;
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 10, backoff = @Backoff(delay = 1000, multiplier = 3))
    public Map<String, Double> getAsyncActualRates() {
        Map<String, Double> rates = getActualRates();
        ExchangeRatesResponse response= webClient
                .get()
                .uri("latest")
                .retrieve()
                .bodyToMono(ExchangeRatesResponse.class)
                .block();
        rates.putAll(response.getRates());
        return rates;
    }
}
