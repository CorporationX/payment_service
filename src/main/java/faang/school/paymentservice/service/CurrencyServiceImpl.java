package faang.school.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CurrencyServiceImpl implements  CurrencyService{
    private static final Map<String, Double> RATES_CACHE = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient;

    @Value("${currency.api.access-key}")
    private String accessKey;

    public CurrencyServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.exchangeratesapi.io/v1")
                .build();
    }

    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void fetchAndStoreRates() {
        log.info("Запрос курсов валют к внешнему API...");
        ExchangeRatesResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest")
                        .queryParam("access_key", accessKey)
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRatesResponse.class)
                .timeout(Duration.ofSeconds(30))
                .block();

        if (response == null || !response.isSuccess() || response.getRates() == null) {
            log.error("Incorrect response from the API: {}", response);
            throw new RuntimeException("Error receiving currency exchange rates");
        }

        RATES_CACHE.clear();
        RATES_CACHE.putAll(response.getRates());
        try {
            String ratesJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(response.getRates());
            log.info("The exchange rates have been updated. Base currency: {}, Date: {}\nRates:\n{}",
                    response.getBase(), response.getDate(), ratesJson);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize currency rates for the log", e);
        }
    }

    public Double getRate(String currencyCode) {
        return RATES_CACHE.get(currencyCode);
    }
}