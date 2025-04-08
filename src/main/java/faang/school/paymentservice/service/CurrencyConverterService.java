package faang.school.paymentservice.service;

import faang.school.paymentservice.client.CurrencyClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import faang.school.paymentservice.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@RequiredArgsConstructor
public class CurrencyConverterService {

    private final CurrencyClient currencyClient;
    private final String appId;

    @Value("${currency.scale}")
    private int scale;

    @Value("${currency.usd-conversion-scale}")
    private int usdConversionScale;

    @Value("${currency.rounding-mode}")
    private RoundingMode roundingMode;

    @Value("${currency.fee-multiplier}")
    private BigDecimal feeMultiplier;

    public BigDecimal convert(PaymentRequest dto, Currency to) {
        BigDecimal amount = dto.amount();
        Currency from = dto.currency();

        ExchangeRatesResponse response = currencyClient.getRates(appId);

        if (response == null || response.rates() == null) {
            log.error("Currency API returned null response or rates");
            throw new IllegalStateException("Invalid currency data");
        }

        BigDecimal fromRate = response.rates().get(from.name());
        BigDecimal toRate = response.rates().get(to.name());

        if (fromRate == null || toRate == null) {
            log.error("Currency not supported: {} or {}", from, to);
            throw new IllegalArgumentException("Currency not supported");
        }

        BigDecimal usdAmount = amount.divide(fromRate, usdConversionScale, roundingMode);
        BigDecimal converted = usdAmount.multiply(toRate);
        BigDecimal withFee = converted.multiply(feeMultiplier);
        return withFee.setScale(scale, roundingMode);
    }
}
