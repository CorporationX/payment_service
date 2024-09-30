package faang.school.paymentservice.service;

import faang.school.paymentservice.client.OpenExchangesRatesClient;
import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    @Value("${currency.exchange.commission}")
    private double commission;
    private OpenExchangesRatesClient ratesClient;

    @Override
    public BigDecimal convertCurrency(String fromCurrency, String toCurrency,
                                      BigDecimal amount, String appId) {
        CurrencyExchangeResponse response = ratesClient.getLatestRates(appId);
        if (!response.getRates().containsKey(toCurrency)) {
            throw new IllegalArgumentException("Currency %d not supported".formatted(toCurrency));
        }
        double fromRate = response.getRates().get(fromCurrency);
        double toRate = response.getRates().get(toCurrency);
        BigDecimal convertedAmount = amount.divide(BigDecimal.valueOf(fromRate),
                4, RoundingMode.HALF_UP);
        BigDecimal bigCommission = BigDecimal.valueOf(commission);
        BigDecimal amountWithCommission = convertedAmount.multiply(bigCommission);

        return amountWithCommission;
    }
}
