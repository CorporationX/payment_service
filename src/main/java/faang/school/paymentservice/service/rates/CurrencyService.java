package faang.school.paymentservice.service.rates;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final ExchangeRatesClient exchangeRatesClient;
    @Value("${currency.exchange.apiKey}")
    private String apiKey;

    @Retryable(retryFor = FeignException.class, maxAttempts = 10, backoff = @Backoff(delay = 1000, multiplier = 3))
    public Map<String, Double> getActualRates() {
        Map<String, Double> rates = new HashMap<>();
        try {
            ExchangeRatesResponse response = exchangeRatesClient.getRates(apiKey);
            if (response != null && response.getRates() != null) {
                rates.putAll(response.getRates());
            }
        } catch (FeignException e) {
            log.error("Failed to fetch rates from external API", e);
        }
        return rates;
    }
}
