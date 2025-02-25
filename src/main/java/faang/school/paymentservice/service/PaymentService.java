package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeCurrencyClient;
import faang.school.paymentservice.config.ExchangeCurrencyProperties;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRateResponse;
import faang.school.paymentservice.exception.ExchangeCurrencyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final ExchangeCurrencyClient currencyClient;
    private final ExchangeCurrencyProperties currencyProperties;

    public BigDecimal convert(BigDecimal amount, Currency fromCurrency, Currency toCurrency) {

        CurrencyRateResponse ratesResponse;
        try{
            ratesResponse = currencyClient.getCurrencyRate(currencyProperties.appId(), fromCurrency.name());
        } catch (RuntimeException e) {
            throw new ExchangeCurrencyException("Exchange rate unavailable");
        }

        Map<String, Double> currentRates = ratesResponse.rates();
        double rate = Optional.ofNullable(currentRates.get(toCurrency.name()))
                .orElseThrow(() -> new ExchangeCurrencyException("Exchange rate for '" + toCurrency + "' not found"));

        BigDecimal convertedAmount = amount.multiply(BigDecimal.valueOf(rate));
        BigDecimal commissionAmount = BigDecimal.valueOf(1 + (currencyProperties.commission() / 100.0));

        return convertedAmount.multiply(commissionAmount).setScale(2, RoundingMode.HALF_UP);
    }
}
