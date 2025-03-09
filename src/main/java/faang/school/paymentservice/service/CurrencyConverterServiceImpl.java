package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeServiceClient;
import faang.school.paymentservice.dto.ExchangeResponse;
import faang.school.paymentservice.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.properties.ExchangeServiceProperties;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyConverterServiceImpl implements CurrencyConverterService {

    private final ExchangeServiceClient exchangeServiceClient;
    private final ExchangeServiceProperties exchangeServiceProperties;

    @Override
    public BigDecimal convertCurrency(PaymentRequest dto) {
        ExchangeResponse response = exchangeServiceClient.exchange(exchangeServiceProperties.getToken());
        Map<String, Double> rates = response.rates();
        BigDecimal fromRate = getRate(rates, dto.fromCurrency());
        BigDecimal toRate = getRate(rates, dto.toCurrency());
        BigDecimal rate = toRate.divide(fromRate, RoundingMode.HALF_UP);
        return calculateAmountWithCommission(dto.amount(), rate);
    }

    private BigDecimal calculateAmountWithCommission(BigDecimal amount, BigDecimal rate) {
        BigDecimal convertedAmount = amount.multiply(rate);
        BigDecimal commission = convertedAmount.multiply(new BigDecimal(exchangeServiceProperties.getCommissionRate()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

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