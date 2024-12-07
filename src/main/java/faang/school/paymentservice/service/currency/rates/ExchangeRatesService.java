package faang.school.paymentservice.service.currency.rates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
@Service
public class ExchangeRatesService {

    @Value("${currency-api.key}")
    private String apiKey;

    @Value("${currency-api.baseCurrency}")
    private Currency baseCurrency;

    private final WebClient webClient;
    private final RedisTemplate<String, Double> redisTemplate;

    @Autowired
    public ExchangeRatesService(WebClient webClient, RedisTemplate<String, Double> redisTemplate) {
        this.webClient = webClient;
        this.redisTemplate = redisTemplate;
    }

    @Retryable(retryFor = {WebClientResponseException.class, WebClientRequestException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public String getExchangeRates() {
        String currencies = Arrays.toString(Currency.values());
        log.info("Attempting to fetch exchange rates...");
        String currencyRates = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("latest")
                        .queryParam("access_key", apiKey)
                        .queryParam("base", baseCurrency.name())
                        .queryParam("symbols", currencies)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        cacheExchangeRates(currencyRates);

        log.debug("Exchange rates updated and cached");
        return currencyRates;
    }

    @Recover
    public String recover(WebClientResponseException ex) {
        log.error("Failed to retrieve exchange rates after retries: {}", ex.getMessage());
        return "{\"success\": false, \"message\": \"Unable to retrieve exchange rates.\"}";
    }

    @Recover
    public String recover(WebClientRequestException ex) {
        log.error("Failed to retrieve exchange rates due to a request issue: {}", ex.getMessage());
        return "{\"success\": false, \"message\": \"Request error while retrieving exchange rates.\"}";
    }

    private void cacheExchangeRates(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode ratesNode = rootNode.path("rates");

            ratesNode.fields().forEachRemaining(entry -> {
                String currency = entry.getKey();
                Double rate = entry.getValue().asDouble();
                redisTemplate.opsForValue().set(currency, rate);
            });
        } catch (JsonProcessingException e) {
            log.error("JSON processing error: ", e);
            throw new RuntimeException(e);
        }
    }
}
