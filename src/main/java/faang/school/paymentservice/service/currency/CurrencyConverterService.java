package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.client.OpenExchangeRatesClient;
import faang.school.paymentservice.dto.LatestRatesResponseDto;
import faang.school.paymentservice.exception.ExchangeRateException;
import faang.school.paymentservice.exception.ExchangeServiceApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyConverterService {
    private final OpenExchangeRatesClient openExchangeRatesClient;
    private static final String TARGET_CURRENCY = "USD";

    public BigDecimal convertCurrency(String fromCurrency, BigDecimal amount) {
        if (fromCurrency.equalsIgnoreCase(TARGET_CURRENCY)) {
            log.info("Skipping exchange rate because from currency is USD!");
            return amount;
        }
        BigDecimal exchangeRateForUSD = getExchangeRateForUSD(fromCurrency);
        int defaultFractionDigits = Currency.getInstance(TARGET_CURRENCY).getDefaultFractionDigits();
        return amount.multiply(exchangeRateForUSD).setScale(defaultFractionDigits, RoundingMode.HALF_UP);
    }

    public BigDecimal getExchangeRateForUSD(String fromCurrency) {
        log.info("Start method getExchangeRateFor USD with fromCurrency: {}", fromCurrency);
        LatestRatesResponseDto latestRates = openExchangeRatesClient.getLatestRates();

        Map<String, BigDecimal> rates = latestRates.getRates();
        if (!Objects.isNull(rates) && !rates.isEmpty()) {
            BigDecimal rateFromUsdToFromCurrency = latestRates.getRates().get(fromCurrency.toUpperCase());
            if (!Objects.isNull(rateFromUsdToFromCurrency)
                    && rateFromUsdToFromCurrency.compareTo(BigDecimal.ZERO) > 0) {
                return BigDecimal.ONE.divide(rateFromUsdToFromCurrency, 10, RoundingMode.HALF_UP);
            }
            log.error("Exchange rate for {} not found or invalid.", fromCurrency);
            throw new ExchangeRateException("Exchange rate for " + fromCurrency + " not found or invalid.");
        }
        log.error("Server error while fetching exchange rates: {}", latestRates.getDisclaimer());
        throw new ExchangeServiceApiException("Server error while fetching exchange rates: "
                + latestRates.getDisclaimer());
    }
}
