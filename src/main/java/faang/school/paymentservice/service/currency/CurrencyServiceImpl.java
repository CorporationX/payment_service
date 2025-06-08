package faang.school.paymentservice.service.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.CurrencyRateResponse;
import faang.school.paymentservice.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CurrencyServiceImpl implements CurrencyService {
    private final WebClient webClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${currency-rates.api.access-key}")
    private String accessKey;


    public CurrencyServiceImpl(WebClient.Builder webClientBuilder,
                               RedisTemplate<String, Object> redisTemplate,
                               ObjectMapper objectMapper) {
        String API_BASE_URL = "https://api.exchangeratesapi.io/v1/latest";
        int TIMEOUT_SECONDS = 10;
        this.webClient = webClientBuilder.baseUrl(API_BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))))
                .build();
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<CurrencyRateResponse> getLatestRates() {
        log.info("Request to Exchange Rates API");
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("access_key", accessKey)
                        .build()
                )
                .retrieve()
                .bodyToMono(CurrencyRateResponse.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
                        .filter(exception -> exception instanceof WebClientResponseException ||
                                        exception instanceof java.net.ConnectException ||
                                        exception instanceof java.util.concurrent.TimeoutException)
                        .onRetryExhaustedThrow((spec, signal) ->
                                signal.failure()
                        )
                        .doBeforeRetry(this::logRetrySignal)
                )
                .doOnSuccess(this::saveToRedis);
    }

    private void logRetrySignal(Retry.RetrySignal signal) {
        log.warn("Attempt #{}, error: {}", signal.totalRetries(), signal.failure().getMessage());
    }

    private void saveToRedis(CurrencyRateResponse response) {
        String redisKey = "exchange_rates_latest";
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(redisKey, response, 24, TimeUnit.HOURS);
            log.info("Saved data in Redis: key='{}', value='{}'", redisKey, json);
        } catch (Exception e) {
            log.error("Error occurred when trying to save data in Redis: {}", e.getMessage());
        }
    }
}