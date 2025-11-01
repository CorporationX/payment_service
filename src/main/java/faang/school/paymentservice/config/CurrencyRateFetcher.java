package faang.school.paymentservice.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.exception.WebClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class CurrencyRateFetcher {
    private final WebClient.Builder webClientBuilder;
    @Value("${current-rate.base-url-rate}")
    private String BASE_URL_RATE;
    private Map<String, Double> mapCurrentRate;

    @Retryable(retryFor = {WebClientRequestException.class, WebClientResponseException.class},
            backoff = @Backoff(delayExpression = "${current-rate.retry-delay}",
                    multiplierExpression = "${current-rate.count-invoke}"))
    @Scheduled(cron = "${current-rate.time-to-invoke}")
    public void currentRate() {
        log.debug("Starting update current rate");
        WebClient webClient = webClientBuilder
                .baseUrl(BASE_URL_RATE)
                .defaultHeader("Accept", "application/json")
                .build();
        String json = webClient.get()
                .uri("_json.js")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("Error on invoke current rate");
            throw new WebClientException("Error json parsing current rate");
        }
        JsonNode currency = root.get("Valute");

        Map<String, Double> result = new HashMap<>();
        currency.fields().forEachRemaining(entry -> {
            String currentCode = entry.getKey();
            double rate = entry.getValue().get("Value").asDouble();
            result.put(currentCode, rate);
        });
        if (!result.isEmpty()) {
            log.debug("Update success current rate");
        }
        mapCurrentRate = result;
    }

    public Map<String, Double> getMapCurrentRate() {
        if (mapCurrentRate == null || mapCurrentRate.isEmpty()) {
            currentRate();
        }
        return mapCurrentRate;
    }
}