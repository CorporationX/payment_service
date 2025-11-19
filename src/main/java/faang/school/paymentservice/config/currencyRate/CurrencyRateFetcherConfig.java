package faang.school.paymentservice.config.currencyRate;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.webClient.WebClientConfig;
import faang.school.paymentservice.exception.WebClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CurrencyRateFetcherConfig {
    private final WebClientConfig webClientConfig;
    private final ObjectMapper getObjectMapper;
    @Value("${current-rate.base-url-rate}")
    private String baseUrl;
    @Value("${current-rate.header}")
    private String header;
    @Value("${current-rate.values}")
    private String values;

    @Retryable(
            retryFor = {WebClientRequestException.class, WebClientResponseException.class},
            backoff = @Backoff(delayExpression = "${current-rate.retry-delay}",
                    multiplierExpression = "${current-rate.count-invoke}"))
    public Map<String, BigDecimal> getCurrentRate() {
        log.debug("Starting update current rate");
        WebClient webClient = webClientConfig.getWebClient(baseUrl, header, values);
        String json = webClient.get()
                .uri("_json.js")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode root;
        try {
            root = getObjectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("Error on invoke current rate");
            throw new WebClientException("Error json parsing current rate");
        }
        JsonNode currency = root.get("Valute");

        Map<String, BigDecimal> result = new HashMap<>();
        currency.fields().forEachRemaining(entry -> {
            String currentCode = entry.getKey();
            BigDecimal rate = BigDecimal.valueOf(entry.getValue().get("Value").asDouble());
            result.put(currentCode, rate);
        });
        result.put("RUB", BigDecimal.ONE);
        log.info("Successfully updated {} currency rates", result.size());
        return result;
    }
}