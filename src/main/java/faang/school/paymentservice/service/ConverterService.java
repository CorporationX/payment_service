package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ConverterClient;
import faang.school.paymentservice.config.CurrencyExchangeConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyExchangeResponse;
import faang.school.paymentservice.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConverterService {
    private final ConverterClient converterClient;
    private final CurrencyExchangeConfig currencyExchangeConfig;

    public CurrencyExchangeResponse getCurrentCurrencyExchangeRate() {

        CurrencyExchangeResponse response = converterClient
                .getCurrentCurrencyExchangeRate(currencyExchangeConfig.appId());
        if (response == null || response.rates() == null || response.rates().isEmpty()) {
            throw new RuntimeException("Не удалось получить корректные курсы валют.");
        }
        return response;
    }

    public BigDecimal convertWithCommission(PaymentRequest dto, Currency targetCurrency) {
        CurrencyExchangeResponse exchangeResponse = getCurrentCurrencyExchangeRate();
        BigDecimal newAmount = getAmountInNewCurrency(dto, targetCurrency, exchangeResponse);
        return addCommission(newAmount);
    }

    private BigDecimal getAmountInNewCurrency(PaymentRequest dto,
                                              Currency targetCurrency,
                                              CurrencyExchangeResponse currentCurrencyExchangeRate) {
        BigDecimal amount = dto.amount();
        BigDecimal targetRate = currentCurrencyExchangeRate.getRate(targetCurrency);
        BigDecimal baseRate = currentCurrencyExchangeRate.getRate(dto.currency());
        if (baseRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Курс базовой валюты не может быть равен нулю.");
        }
        return amount.multiply(targetRate).divide(baseRate, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal addCommission(BigDecimal amount) {
        BigDecimal commissionMultiplier = BigDecimal.ONE
                .add(BigDecimal.valueOf(currencyExchangeConfig.commission())
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return amount.multiply(commissionMultiplier);
    }
}
