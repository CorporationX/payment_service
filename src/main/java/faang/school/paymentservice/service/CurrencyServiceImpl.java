package faang.school.paymentservice.service;

import faang.school.paymentservice.config.CurrencyExchangeConfig;
import faang.school.paymentservice.client.CurrencyConverterClient;
import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.dto.response.CurrencyExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String CONVERTING_MONEY_MESSAGE = "Dear friend! Thank you for converting money! You converted %s %s to %s %s with commission %f%%";

    private final CurrencyExchangeConfig config;
    private final CurrencyConverterClient currencyConverter;

    public CurrencyExchangeResponse getCurrentCurrencyExchangeRate() {
        return currencyConverter.getCurrentCurrencyExchangeRate(config.getAppId());
    }

    public BigDecimal convertWithCommission(PaymentRequestDto dto, Currency targetCurrency) {
        BigDecimal newAmount = getAmountInNewCurrency(dto, targetCurrency, getCurrentCurrencyExchangeRate());
        return addCommision(newAmount);
    }

    public String createMessage(PaymentRequestDto dto, BigDecimal newAmount, Currency targetCurrency) {
        return String.format(
                CONVERTING_MONEY_MESSAGE,
                DECIMAL_FORMAT.format(dto.getAmount()),
                dto.getCurrency(),
                DECIMAL_FORMAT.format(newAmount),
                targetCurrency,
                config.getCommission()
        );
    }

    private BigDecimal addCommision(BigDecimal amount) {
        BigDecimal commission = BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(config.getCommission() / 100.0));
        return amount.multiply(commission);
    }

    private BigDecimal getAmountInNewCurrency(PaymentRequestDto dto, Currency targetCurrency, CurrencyExchangeResponse currentCurrencyExchange) {
        BigDecimal amount = dto.getAmount();
        BigDecimal targetRate = currentCurrencyExchange.getRate(targetCurrency);
        BigDecimal baseRate = currentCurrencyExchange.getRate(dto.getCurrency());
        return (amount.multiply(targetRate)).divide(baseRate, 2, RoundingMode.HALF_UP);
    }
}
