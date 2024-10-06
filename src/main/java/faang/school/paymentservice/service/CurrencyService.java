package faang.school.paymentservice.service;

import faang.school.paymentservice.config.CurrencyExchangeConfig;
import faang.school.paymentservice.client.CurrencyConverterClient;
import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.response.CurrencyExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyExchangeConfig config;
    private final CurrencyConverterClient currencyConverter;

    public CurrencyExchangeResponse getCurrentCurrencyExchangeRate() {
        return currencyConverter.getCurrentCurrencyExchangeRate(config.getAppId());
    }

    public BigDecimal convertWithCommission(PaymentRequestDto dto, Currency targetCurrency) {
        BigDecimal newAmount = getAmountInNewCurrency(dto, targetCurrency, getCurrentCurrencyExchangeRate());
        return addCommision(newAmount);
    }

    private BigDecimal addCommision(BigDecimal amount) {
        BigDecimal commission = BigDecimal.valueOf(1).add(BigDecimal.valueOf(exchangeConfig.getCommission() / 100.0));
        return amount.multiply(commission);
    }

    private BigDecimal getAmountInNewCurrency(
            PaymentRequestDto dto,
            Currency targetCurrency,
            CurrencyExchangeResponse currentCurrencyExchange
    ) {
        BigDecimal amount = dto.getAmount();
        BigDecimal targetRate = currentCurrencyExchange.getRate(targetCurrency);
        BigDecimal baseRate = currentCurrencyExchange.getRate(dto.getCurrency());
        return (amount.multiply(targetRate)).divide(baseRate, 2, RoundingMode.HALF_UP);
    }
}
