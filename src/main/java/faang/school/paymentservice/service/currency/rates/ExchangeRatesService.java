package faang.school.paymentservice.service.currency.rates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ExchangeRatesService {

    @Value("${api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final RedisTemplate<String, BigDecimal> redisTemplate;

    @Autowired
    public ExchangeRatesService(WebClient.Builder webClientBuilder,
                                RedisTemplate<String, BigDecimal> redisTemplate) {
        this.webClient = webClientBuilder.baseUrl("http://api.exchangeratesapi.io/v1/").build();
        this.redisTemplate = redisTemplate;
    }

    @Retryable(retryFor = {WebClientResponseException.class, ResourceAccessException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public void getExchangeRates() {
        String currencies = Arrays.stream(Currency.values())
                .map(Enum::name)
                .collect(Collectors.joining(","));

        Mono<String> currencyRates = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("latest")
                        .queryParam("access_key", apiKey)
                        .queryParam("symbols", currencies)
                        .build())
                .retrieve()
                .bodyToMono(String.class);

        cacheExchangeRates(currencyRates);
    }

    private void cacheExchangeRates(Mono<String> currencyRatesMono) {
        currencyRatesMono.subscribe(jsonString -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonString);
                JsonNode ratesNode = rootNode.path("rates");

                ratesNode.fields().forEachRemaining(entry -> {
                    String currency = entry.getKey();
                    BigDecimal rate = BigDecimal.valueOf(entry.getValue().asDouble());
                    redisTemplate.opsForValue().set(currency, rate);
                });
            } catch (JsonProcessingException e) {
                log.error("JSON processing error: ", e);
                throw new RuntimeException(e);
            }
        });
    }
}
