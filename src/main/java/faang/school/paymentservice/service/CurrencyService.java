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

@Service
@RequiredArgsConstructor
@Slf4j
@Data
public class CurrencyService {
    private final CurrencyConverterClient currencyConverterClient;
    private final CurrencyExchangeConfig exchangeConfig;
    private CurrencyExchangeResponse currentCurrencyExchange;

    @PostConstruct
    public void postConstruct() {
        getCurrentCurrencyExchangeRate();
    }

    public CurrencyExchangeResponse getCurrentCurrencyExchangeRate() {
        currentCurrencyExchange = currencyConverterClient.getCurrentCurrencyExchangeRate(exchangeConfig.getAppId());
        return currentCurrencyExchange;
    }

    public BigDecimal convertWithCommission(PaymentRequest dto, Currency targetCurrency) {
        BigDecimal newAmount = getAmountInNewCurrency(dto, targetCurrency, currentCurrencyExchange);
        return addCommision(newAmount);
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