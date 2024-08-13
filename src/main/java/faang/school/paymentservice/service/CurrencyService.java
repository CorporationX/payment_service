package faang.school.paymentservice.service;

import faang.school.paymentservice.client.CurrencyConverterClient;
import faang.school.paymentservice.config.currency.CurrencyExchangeConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Service
@RequiredArgsConstructor
@Slf4j
@Data
public class CurrencyService {
    private final CurrencyConverterClient currencyConverterClient;
    private final CurrencyExchangeConfig exchangeConfig;
    private CurrencyExchangeResponse currentCurrencyExchange;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String CONVERTING_MONEY_MESSAGE = "Dear friend! Thank you for converting money! You converted %s %s to %s %s with commission %f%%";


    @PostConstruct
    public void postConstruct() {
        getCurrentCurrencyExchangeRate();
    }

    public CurrencyExchangeResponse getCurrentCurrencyExchangeRate() {
        currentCurrencyExchange = currencyConverterClient.getCurrentCurrencyExchangeRate(exchangeConfig.getAppId());
        return currentCurrencyExchange;
    }

    public String convertWithCommission(PaymentRequest dto, Currency targetCurrency) {
        BigDecimal newAmount = getAmountInNewCurrency(dto, targetCurrency, currentCurrencyExchange);
        BigDecimal newAmountWithComission = addCommision(newAmount);

        String message = String.format(
                CONVERTING_MONEY_MESSAGE,
                DECIMAL_FORMAT.format(dto.amount()),
                dto.currency(),
                DECIMAL_FORMAT.format(newAmountWithComission),
                targetCurrency,
                exchangeConfig.getCommission()
        );
        return message;
    }

    private BigDecimal addCommision(BigDecimal amount) {
        BigDecimal commission = BigDecimal.valueOf(1).add(BigDecimal.valueOf(exchangeConfig.getCommission() / 100.0));
        return amount.multiply(commission);
    }

    private BigDecimal getAmountInNewCurrency(
            PaymentRequest dto,
            Currency targetCurrency,
            CurrencyExchangeResponse currentCurrencyExchange
    ) {
        BigDecimal amount = dto.amount();
        BigDecimal targetRate = currentCurrencyExchange.getRate(targetCurrency);
        BigDecimal baseRate = currentCurrencyExchange.getRate(dto.currency());
        return (amount.multiply(targetRate)).divide(baseRate, 2, RoundingMode.HALF_UP);
    }
}
