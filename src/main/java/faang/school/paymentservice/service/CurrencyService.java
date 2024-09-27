package faang.school.paymentservice.service;

import faang.school.paymentservice.client.CurrencyConverterClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class CurrencyService {
    private final CurrencyConverterClient currencyConverterClient;
    private CurrencyExchangeResponse currentCurrencyExchange;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String CONVERTING_MONEY_MESSAGE = "Dear friend! Thank you for converting money! You converted %s %s to %s %s with commission %f%%";

    @Value("${currency.fetch.url}")
    private String url;
    @Value("${currency.fetch.appId}")
    private String appId;
    @Value("${currency.fetch.commission}")
    private Double commission;

    @PostConstruct
    public void postConstruct() {
        getCurrentCurrencyExchangeRate();
    }

    public CurrencyExchangeResponse getCurrentCurrencyExchangeRate() {
        currentCurrencyExchange = currencyConverterClient.getCurrentCurrencyExchangeRate(appId);
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
                commission
        );
        return message;
    }

    private BigDecimal addCommision(BigDecimal amount) {
        BigDecimal commissionResult = BigDecimal.valueOf(1).add(BigDecimal.valueOf(commission / 100.0));
        return amount.multiply(commissionResult);
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