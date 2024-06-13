package faang.school.paymentservice.services;

import faang.school.paymentservice.client.OpenExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenExchangeRatesService {
    private final OpenExchangeRatesClient openExchangeRatesClient;
    @Value("${payment.exchange_appId}")
    private String appId;

    @Retryable(value = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public BigDecimal exchange(Currency currencyFrom, Currency currencyTo) {
        CurrencyResponse rates = openExchangeRatesClient.getRates(
                appId,
                currencyFrom,
                List.of(currencyTo)
        );
        if (rates.getRates().isEmpty()) {
            String errorMessage = "Could not get exchange rates from Open Exchange Rates API";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        return BigDecimal.valueOf(rates.getRates().get(currencyTo));
    }
}
