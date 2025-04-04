package faang.school.paymentservice.service;

import faang.school.paymentservice.client.CurrencyClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import faang.school.paymentservice.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@RequiredArgsConstructor
public class CurrencyConverterService {

    private final CurrencyClient currencyClient;
    private final String appId;

    public BigDecimal convert(PaymentRequest dto, Currency to) {
        BigDecimal amount = dto.amount();
        Currency from = dto.currency();

        ExchangeRatesResponse response = currencyClient.getRates(appId);
        Double fromRate = response.rates().get(from.name());
        Double toRate = response.rates().get(to.name());

        if (fromRate == null || toRate == null) {
            log.error("Currency not supported: {} or {}", from, to);
            throw new IllegalArgumentException("Currency not supported");
        }

        BigDecimal usdAmount = amount.divide(BigDecimal.valueOf(fromRate), 10, RoundingMode.HALF_UP);
        BigDecimal converted = usdAmount.multiply(BigDecimal.valueOf(toRate));
        BigDecimal withFee = converted.multiply(BigDecimal.valueOf(1.01));
        return withFee.setScale(2, RoundingMode.HALF_UP);
    }
}
