package faang.school.paymentservice.service.exchange;

import faang.school.paymentservice.client.CurrencyClient;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.exchange.CurrencyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final CurrencyClient currencyClient;
    @Value("${app.currency.base}")
    private String baseCurrency;
    @Value("${app.currency.commission}")
    private BigDecimal commission;
    @Value("${services.currencyClient.appId}")
    private String appId;

    public BigDecimal getAmountInBaseCurrency(PaymentRequest dto) {
        String targetCurrency = dto.currency().name();
        if (targetCurrency.equals(baseCurrency)) {
            return dto.amount();
        }
        CurrencyResponse response = currencyClient.getCurrencyRates(appId, baseCurrency, targetCurrency);
        BigDecimal rate = response.getRate(targetCurrency);
        BigDecimal convertedAmount = dto.amount().multiply(rate);
        BigDecimal amountWithCommission = convertedAmount.multiply(commission);

        return amountWithCommission.setScale(2, RoundingMode.HALF_UP);
    }
}
