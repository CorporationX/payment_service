package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateResponse;
import faang.school.paymentservice.exception.CurrencyRatesUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyService {
    private final WebClient webClient;
    private final Map<Currency, BigDecimal> currencyRates = new ConcurrentHashMap<>();

    @Value("${app.api.accessKey}")
    private String accessKey;

    @Retryable(value = {Exception.class}, backoff = @Backoff(delay = 3000, multiplier = 2))
    public void fetchCurrencyRates() {
        log.info("Starting to fetch currency rates.");
        ExchangeRateResponse response = webClient
                .get()
                .uri(builder ->
                    builder
                            .path("/v1/latest")
                            .queryParam("access_key", accessKey)
                            .queryParam("base", "EUR")
                            .build())
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .block();
        if (response != null && response.getRates() != null) {
            log.info("Successfully fetched currency rates: {}", response.getRates());
            for (Currency currency : Currency.values()) {
                BigDecimal rate = response.getRates().getOrDefault(currency, BigDecimal.ZERO);
                currencyRates.put(currency, rate);
                log.info("Updated rate for {}: {}", currency, rate);
            }
        } else {
            log.error("Currency rates not available");
            throw new CurrencyRatesUnavailableException("Currency rates not available");
        }
        log.info("Currency rates update completed.");
    }

    public Map<Currency, BigDecimal> getCurrencyRates() {
        return Collections.unmodifiableMap(currencyRates);
    }
}
