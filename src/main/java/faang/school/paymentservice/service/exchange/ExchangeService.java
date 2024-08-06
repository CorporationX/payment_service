package faang.school.paymentservice.service.exchange;

import faang.school.paymentservice.client.CurrencyClient;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.exchange.CurrencyResponse;
import faang.school.paymentservice.exception.CurrencyConversionException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
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

        CurrencyResponse response = getCurrencyRates(targetCurrency);

        BigDecimal rate = response.getRate(targetCurrency);
        if (rate == null) {
            log.error("No exchange rate available for " + targetCurrency);
            throw new CurrencyConversionException("No exchange rate available for " + targetCurrency);
        }

        BigDecimal convertedAmount = dto.amount().divide(rate, MathContext.DECIMAL128);
        BigDecimal amountWithCommission = convertedAmount.multiply(commission);

        return amountWithCommission.setScale(2, RoundingMode.HALF_UP);
    }

    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 1000, multiplier = 2))
    private CurrencyResponse getCurrencyRates(String targetCurrency) {
        return currencyClient.getCurrencyRates(appId, baseCurrency, targetCurrency);
    }


    public boolean isCurrencyBase(PaymentRequest dto) {
        return dto.currency().name().equals(baseCurrency);
    }
}
