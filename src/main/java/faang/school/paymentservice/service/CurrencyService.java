package faang.school.paymentservice.service;

import faang.school.paymentservice.config.properties.CurrencyApiProperties;
import faang.school.paymentservice.config.properties.RetryProperties;
import faang.school.paymentservice.dto.CurrencyRatesResponse;
import faang.school.paymentservice.exception.CurrencyRateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Arrays;


@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final WebClient currencyWebClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CurrencyApiProperties currencyApiProperties;
    private final RetryProperties retryProperties;

    public CurrencyRatesResponse fetchAndSaveRates() {
        try {
            return fetchCurrencyRates()
                    .doOnSuccess(this::saveCurrencyRatesToRedis)
                    .doOnError(error ->
                            log.error("Error fetching currency rates: {}", error.getMessage(), error)
                    )
                    .block();
        } catch (Exception e) {
            log.error("Exception in fetchAndSaveRates: {}", e.getMessage(), e);
            throw new CurrencyRateException("Temporary we accept only the following currencies: " + currencyApiProperties.getBaseCurrency());
        }
    }

    public CurrencyRatesResponse getCurrencyRatesFromRedis() {
        try {
            CurrencyRatesResponse response = (CurrencyRatesResponse) redisTemplate.opsForValue().get(currencyApiProperties.getRedisKey());
            if (response == null) {
                log.warn("Warning Redis does not contain currency key {}", currencyApiProperties.getRedisKey());
                response = fetchAndSaveRates();
            }
            return response;
        } catch (Exception e) {
            log.error("Error getting currency rates from Redis: {}", e.getMessage(), e);
            throw new CurrencyRateException("Temporary we accept only the following currencies: " + currencyApiProperties.getBaseCurrency());
        }
    }

    public BigDecimal convertToBaseCurrency(BigDecimal amount, String sourceCurrency) {
        if (isConversionRequired(sourceCurrency)) {
            CurrencyRatesResponse ratesResponse = getCurrencyRatesFromRedis();
            return amount.divide(getExchangeRate(sourceCurrency, ratesResponse), 5, RoundingMode.HALF_UP);
        }
        return amount;
    }

    private BigDecimal getExchangeRate(String sourceCurrency, CurrencyRatesResponse ratesResponse) {
        if (!ratesResponse.getRates().containsKey(sourceCurrency.toUpperCase())) {
            throw new CurrencyRateException(
                    String.format("Exchange rate for currency '%s' is not available. Available only following currencies: %s",
                            sourceCurrency, Arrays.toString(ratesResponse.getRates().keySet().toArray()))
            );
        }
        return ratesResponse.getRates().get(sourceCurrency.toUpperCase());
    }

    private boolean isConversionRequired(String currencyCode) {
        return !currencyCode.equals(currencyApiProperties.getBaseCurrency());
    }

    private Mono<CurrencyRatesResponse> fetchCurrencyRates() {
        String uri = "latest?access_key=" + currencyApiProperties.getApiKey();

        log.info("Fetching currency rates from API: {}", currencyApiProperties.getUrl() + uri);

        return currencyWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CurrencyRatesResponse.class)
                .timeout(Duration.ofMillis(currencyApiProperties.getTimeout().getRead()))
                .retryWhen(
                        Retry.fixedDelay(retryProperties.getRetry(), Duration.ofMillis(retryProperties.getDelay()))
                                .doBeforeRetry(retrySignal ->
                                        log.warn("Retrying fetchCurrencyRates due to error: {}", retrySignal.failure().getMessage())
                                )
                )
                .doOnError(
                        error -> log.error("Error fetching currency rates after retries: {}", error.getMessage(), error));
    }

    private void saveCurrencyRatesToRedis(CurrencyRatesResponse response) {
        log.info("Saving currency rates to Redis: {}", response);

        try {
            redisTemplate.opsForValue().set(currencyApiProperties.getRedisKey(), response);
            log.info("Successfully fetched and saved currency rates.");
        } catch (Exception e) {
            log.error("Error saving currency rates to Redis: {}", e.getMessage(), e);
        }
    }
}

