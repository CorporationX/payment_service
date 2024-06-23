package faang.school.paymentservice.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import faang.school.paymentservice.client.CurrencyConverterClient;
import faang.school.paymentservice.config.currency.CurrencyExchangeConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConverterService {
    private final CurrencyConverterClient currencyConverterClient;
    private final CurrencyExchangeConfig exchangeConfig;
    
    public CurrencyExchangeResponse getCurrentCurrencyExchange() {
        return currencyConverterClient.getCurrentCurrencyExchange(exchangeConfig.getAppId());
    }
    
    public BigDecimal convertWithCommission(PaymentRequest dto, Currency targetCurrency) {
        BigDecimal newAmount = getAmountInNewCurrency(dto, targetCurrency, getCurrentCurrencyExchange());
        return addCommision(newAmount);
    }
    
    private BigDecimal addCommision(BigDecimal amount) {
        return amount.multiply(new BigDecimal(1).add(new BigDecimal( exchangeConfig.getCommission() / 100.0))).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    private BigDecimal getAmountInNewCurrency(
        PaymentRequest dto,
        Currency targetCurrency,
        CurrencyExchangeResponse currentCurrencyExchange
    ) {
        BigDecimal amount = dto.amount();
        BigDecimal targetRate = currentCurrencyExchange.getRate(targetCurrency);
        BigDecimal baseRate = currentCurrencyExchange.getRate(dto.currency());
        return (amount.multiply(targetRate)).divide(baseRate,2, BigDecimal.ROUND_HALF_UP);
    }
}
