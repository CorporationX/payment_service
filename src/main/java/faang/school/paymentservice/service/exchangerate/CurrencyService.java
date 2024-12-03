package faang.school.paymentservice.service.exchangerate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.exception.CurrencyRateException;
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

    @Value("${currency.rate.baseUrl}")
    private String currencyBaseUrl;

    @Value("${currency.rate.api-key}")
    private String key_api;

    @Value("${currency.rate.symbols}")
    private String symbolsCurrency;

    private final ObjectMapper mapper = new ObjectMapper();

    private final WebClient webClient;

    @Retryable(retryFor = WebClientRequestException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void fetchCurrency() {
        log.info("Fetch currency rate");
        String json = webClient.get()
                .uri(currencyBaseUrl + "latest&?access_key=" + key_api + "&symbols=" + symbolsCurrency)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        try {
            JsonNode baseNode = mapper.readTree(json);
            JsonNode rates = baseNode.get("rates");
            rates.fields().forEachRemaining(entry -> CURRENCIES.put(entry.getKey(), entry.getValue().asDouble()));
        } catch (JsonProcessingException e) {
            throw new CurrencyRateException("Error processing currency rates", e);
        } catch (Exception e) {
            throw new CurrencyRateException("Unexpected error fetching currency rates", e);
        }
    }
}