package faang.school.paymentservice.service;

import faang.school.paymentservice.client.OpenExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import faang.school.paymentservice.exception.NoSuchExchangeRateException;
import faang.school.paymentservice.exception.OpenExchangeRatesException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRatesService {
    private final OpenExchangeRatesClient openExchangeRatesClient;

    public BigDecimal getExchangeRate(Currency paymentCurrency, Currency targetCurrency) {
        ExchangeRatesResponse exchangeRates;
        try {
            exchangeRates = openExchangeRatesClient.getExchangeRates(paymentCurrency, targetCurrency);
        } catch (FeignException e) {
            log.error("Failed to get exchange rates from OpenExchangeRates!", e);
            throw new OpenExchangeRatesException("Failed to get exchange rates from OpenExchangeRates!", e);
        }

        return Optional.ofNullable(exchangeRates.getRates().get(targetCurrency))
                .orElseThrow(() -> {
                    log.error("Exchange rate for currency {} not found", targetCurrency);
                    return new NoSuchExchangeRateException("Exchange rate for currency " + targetCurrency
                            + " is not available");
                });
    }
}
