package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeClient;
import faang.school.paymentservice.exception.CurrencyConversionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyConverterService {

    @Value("${openexchangerates.app-id}")
    private String appId;
    @Value("${app.target-currency}")
    private String targetCurrency;
    @Value("${app.commission-rate}")
    private BigDecimal commissionRate;
    private static final int DIVIDE_SCALE = 8;
    private static final int SCALE = 2;

    private final ExchangeClient exchangeClient;

    public BigDecimal convertToRub(String fromCurrency, BigDecimal amount) {
        Map<String, Object> response = exchangeClient.getLatestRates(appId);

        Object ratesObj = response.get("rates");
        if (!(ratesObj instanceof Map<?, ?> rawRates)) {
            log.error("Rates data is missing or invalid: {}", ratesObj);
            throw new CurrencyConversionException("Rates data is missing or invalid");
        }

        Map<String, Double> rates = rawRates.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> {
                            Object val = e.getValue();
                            if (val instanceof Number number) return number.doubleValue();
                            log.error("Invalid rate value for {}: {}", e.getKey(), val);
                            throw new CurrencyConversionException("Invalid rate value for ".formatted(e.getKey()));
                        }
                ));

        if (!rates.containsKey(fromCurrency) || !rates.containsKey(targetCurrency)) {
            log.error("Exchange rate not found for {} or {}", fromCurrency, targetCurrency);
            throw new CurrencyConversionException("Exchange rate not available for currency: ".formatted(fromCurrency));
        }

        BigDecimal fromRate = BigDecimal.valueOf(rates.get(fromCurrency));
        BigDecimal rubRate = BigDecimal.valueOf(rates.get(targetCurrency));

        BigDecimal amountInUsd = amount.divide(fromRate, DIVIDE_SCALE, RoundingMode.HALF_UP);
        BigDecimal amountInRub = amountInUsd.multiply(rubRate);

        BigDecimal finalAmount = amountInRub.multiply(BigDecimal.ONE.add(commissionRate));

        return finalAmount.setScale(SCALE, RoundingMode.HALF_UP);
    }
}
