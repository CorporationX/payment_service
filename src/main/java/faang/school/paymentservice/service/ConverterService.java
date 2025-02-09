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
        return converterClient.getCurrentCurrencyExchangeRate(currencyExchangeConfig.appId());
    }

    public BigDecimal convertWithCommission(PaymentRequest dto, Currency targetCurrency) {
        BigDecimal newAmount = getAmountInNewCurrency(dto, targetCurrency, getCurrentCurrencyExchangeRate());
        return addCommission(newAmount);
    }

    private BigDecimal getAmountInNewCurrency(PaymentRequest dto,
                                              Currency targetCurrency,
                                              CurrencyExchangeResponse currentCurrencyExchangeRate) {
        BigDecimal amount = dto.amount();
        BigDecimal targetRate = currentCurrencyExchangeRate.getRate(targetCurrency);
        BigDecimal baseRate = currentCurrencyExchangeRate.getRate(dto.currency());
        return amount.multiply(targetRate).divide(baseRate, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal addCommission(BigDecimal amount) {
        BigDecimal commission = BigDecimal.valueOf(1).add(BigDecimal.valueOf(currencyExchangeConfig.commission() / 100));
        return amount.multiply(commission);
    }
}
