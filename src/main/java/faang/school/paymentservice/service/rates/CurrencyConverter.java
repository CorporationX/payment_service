package faang.school.paymentservice.service.rates;

import faang.school.paymentservice.dto.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CurrencyConverter {
    private final ExchangeRatesCash exchangeRatesCash;
    @Value("${currency.exchange.convert.scale}")
    private int scale;
    @Value("${currency.exchange.convert.roundingMode}")
    private String roundingMode;

    public BigDecimal convert(Currency toCurrency, Currency fromCurrency, BigDecimal amount) {
        double reitFrom = getCurrencyReit(fromCurrency);
        double reitTO = getCurrencyReit(toCurrency);
        return BigDecimal.valueOf(reitTO / reitFrom).multiply(amount).setScale(scale, RoundingMode.valueOf(roundingMode));
    }

    private double getCurrencyReit(Currency currency) {
        return exchangeRatesCash.getRates().get(currency.name());
    }

}
