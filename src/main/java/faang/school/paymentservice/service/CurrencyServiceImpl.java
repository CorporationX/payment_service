package faang.school.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.CurrencyApiProperties;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private static final Map<String, Double> RATES_CACHE = new ConcurrentHashMap<>();
    private final ExchangeRatesClient exchangeRatesClient;
    private final CurrencyApiProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    @Retryable(
            maxAttemptsExpression = "#{@currencyApiProperties.retry.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "#{@currencyApiProperties.retry.delay}",
                    multiplierExpression = "#{@currencyApiProperties.retry.multiplier}"
            )
    )
    public void fetchAndStoreRates() {
        log.info("Requesting exchange rates to external API...");
        log.info("Sending request to url: {}", properties.getUrl());
        ExchangeRatesResponse response = exchangeRatesClient.getResponse();

        if (response == null || response.getRates() == null) {
            log.error("Invalid API response: {}", response);
            throw new RuntimeException("Failed to fetch currency rates");
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

    @Override
    public Double getRate(String currencyCode) {
        return RATES_CACHE.get(currencyCode);
    }
}