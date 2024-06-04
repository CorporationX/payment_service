package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import faang.school.paymentservice.validator.CurrencyValidator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Data
public class CurrencyConverter {
    private final ExchangeRatesClient exchangeRatesClient;
    private final CurrencyValidator currencyValidator;

    @Value("${services.openexchangerates.app_id}")
    private String appId;

    @Value("${currencyConverter.commission}")
    private BigDecimal commission;

    public BigDecimal converter(Currency fromCurrency, Currency toCurrency, BigDecimal amount) {
        ExchangeRatesResponse exchangeRatesResponse = exchangeRatesClient.getRates(appId);
        Map<String, Double> rates = exchangeRatesResponse.getRates();

        currencyValidator.checkExistCurrency(fromCurrency, toCurrency, rates);

        double fromRate = rates.get(fromCurrency.name());
        double toRate = rates.get(toCurrency.name());

        return BigDecimal.valueOf(toRate / fromRate).multiply(amount).multiply(commission);
    }
}