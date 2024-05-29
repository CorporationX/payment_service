package faang.school.paymentservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private static final Map<String, Double> CURRENCIES = new HashMap<>();

    @Value("${currency.base_url}")
    private String currencyBaseUrl;

    @Value("${currency.key_api}")
    private String key_api;

    private final ObjectMapper mapper = new ObjectMapper();

    private final WebClient webClient;

    @Retryable(retryFor = WebClientRequestException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void fetchCurrency() {
        log.info("Fetch currency rate");
        String json = webClient.get()
                .uri(currencyBaseUrl + key_api + "/latest/USD")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode baseNode = mapper.readTree(json);
            JsonNode rates = baseNode.get("conversion_rates");
            rates.fields().forEachRemaining(entry -> {
                CURRENCIES.put(entry.getKey(), entry.getValue().asDouble());
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public Map<String, Double> getCurrencies() {
        return CURRENCIES;
    }
}
