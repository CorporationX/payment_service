package faang.school.paymentservice.service.exchange;

import faang.school.paymentservice.client.OpenExchangeClient;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.exchange.CurrencyResponse;
import faang.school.paymentservice.exception.CurrencyConversionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {
    private final OpenExchangeClient currencyClient;
    @Value("${app.currency.base}")
    private String baseCurrency;
    @Value("${app.currency.commission}")
    private BigDecimal commission;

    public BigDecimal getAmountInBaseCurrency(PaymentRequest dto) {
        String targetCurrency = dto.currency().name();

        CurrencyResponse response = currencyClient.getCurrencyRates(targetCurrency).orElseThrow(
                () -> new CurrencyConversionException("Something went wrong by fetching currency " + targetCurrency));

        BigDecimal rate = response.getRate(targetCurrency);
        BigDecimal convertedAmount = dto.amount().divide(rate, MathContext.DECIMAL128);
        BigDecimal amountWithCommission = convertedAmount.multiply(commission);

        return amountWithCommission.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isCurrencyBase(PaymentRequest dto) {
        return dto.currency().name().equals(baseCurrency);
    }
}
