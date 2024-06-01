package faang.school.paymentservice.service.converter;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CurrencyConverter {
    private final ExchangeRatesClient exchangeRatesClient;

    @Value("${services.openexchangerates.app_id}")
    private String appId;

    @Value("${currencyConverter.commission}")
    private BigDecimal commission;

    public BigDecimal convert(Currency fromCurrency, Currency toCurrency, BigDecimal amount){
        ExchangeRatesResponse exchangeRatesResponse = exchangeRatesClient.getRates(appId);
        Map<String, Double> rates = exchangeRatesResponse.getRates();

        double fromRate = rates.get(fromCurrency.name());
        double toRate = rates.get(toCurrency.name());

        return BigDecimal.valueOf(toRate / fromRate).multiply(amount).multiply(commission);
    }
}
