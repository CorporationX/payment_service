package faang.school.paymentservice.service.rates;

import faang.school.paymentservice.client.config.ExchangeRatesInterceptor;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CurrencyConverter {
    private final ExchangeRatesCash exchangeRatesCash;
    private final ExchangeRatesInterceptor exchangeRatesInterceptor;

    @Value("${currency.exchange.convert.scale}")
    private int scale;
    @Value("${currency.exchange.convert.roundingMode}")
    private String roundingMode;
    @Value("${convector.base_commission}")
    private double commission;
    @Value("${convector.base_currency}")
    private String baseCurrency;

    public BigDecimal convert(Currency toCurrency, Currency fromCurrency, BigDecimal amount) {
        double reitFrom = getCurrencyReit(fromCurrency);
        double reitTO = getCurrencyReit(toCurrency);
        return BigDecimal.valueOf(reitTO / reitFrom).multiply(amount).setScale(scale, RoundingMode.valueOf(roundingMode));
    }

    public PaymentResponse sendPayment(PaymentRequest dto) {
        BigDecimal amountAfterConvert = calculateConversion(dto.currency(), dto.amount());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(dto.amount());
        String formattedAmountAfterConvert = decimalFormat.format(amountAfterConvert);
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted. " + "Including comic convector completed %s %s",
                formattedSum, dto.currency().name(), formattedAmountAfterConvert, baseCurrency);

        return new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                message);
    }

    private double getCurrencyReit(Currency currency) {
        return exchangeRatesCash.getRates().get(currency.name());
    }

    private BigDecimal calculateConversion(Currency fromCurrency, BigDecimal amount) {
        BigDecimal exchangeRate = exchangeRatesInterceptor.getExchangeRate(baseCurrency, fromCurrency.toString());
        BigDecimal convertAmount = amount.divide(exchangeRate, MathContext.DECIMAL128);
        BigDecimal ValueCommission = convertAmount.multiply(BigDecimal.valueOf(commission));
        return convertAmount.subtract(ValueCommission);
    }

}
