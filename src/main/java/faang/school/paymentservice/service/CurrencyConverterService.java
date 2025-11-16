package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeClient;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
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
        ExchangeRatesResponse response = exchangeClient.getLatestRates(appId);

        Map<String, BigDecimal> rates = response.rates();
        if (rates == null || rates.isEmpty()) {
            log.error("Rates data is missing or invalid: {}", response);
            throw new CurrencyConversionException("Rates data is missing or invalid");
        }

        if (!rates.containsKey(fromCurrency) || !rates.containsKey(targetCurrency)) {
            log.error("Exchange rate not found for {} or {}", fromCurrency, targetCurrency);
            throw new CurrencyConversionException(
                    "Exchange rate not available for currency: %s".formatted(fromCurrency)
            );
        }

        BigDecimal fromRate = rates.get(fromCurrency);
        BigDecimal rubRate = rates.get(targetCurrency);

        BigDecimal amountInUsd = amount.divide(fromRate, DIVIDE_SCALE, RoundingMode.HALF_UP);
        BigDecimal amountInRub = amountInUsd.multiply(rubRate);

        BigDecimal finalAmount = amountInRub.multiply(BigDecimal.ONE.add(commissionRate));

        return finalAmount.setScale(SCALE, RoundingMode.HALF_UP);
    }
}
