package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeServiceClient;
import faang.school.paymentservice.dto.ExchangeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import faang.school.paymentservice.dto.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyConverterService {

    private final ExchangeServiceClient exchangeServiceClient;
    @Value("${services.exchange-service.token}")
    private final String token;

    @Value("${services.exchange-service.commission-rate}")
    private final double commissionRate;

    public BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency, BigDecimal amount) {
        ExchangeResponse response = exchangeServiceClient.exchange(token);
        Map<String, Double> rates = response.rates();

        BigDecimal fromRate = getRate(rates, fromCurrency);
        BigDecimal toRate = getRate(rates, toCurrency);

        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("Exchange rate not found for specified currencies: " + fromCurrency.name() + " to " + toCurrency.name());
        }

        BigDecimal rate = toRate.divide(fromRate, RoundingMode.HALF_UP);

        BigDecimal convertedAmount = amount.multiply(rate);
        BigDecimal commission = convertedAmount.multiply(new BigDecimal(commissionRate)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return convertedAmount.add(commission);
    }

    private BigDecimal getRate(Map<String, Double> rates, Currency currency) {
        Double rate = rates.get(currency.name());
        if (rate == null) {
            throw new IllegalArgumentException("Exchange rate not found for currency: " + currency.name());
        }
        return BigDecimal.valueOf(rate);
    }
}