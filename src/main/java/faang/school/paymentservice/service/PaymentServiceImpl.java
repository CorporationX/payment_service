package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.currency_converter.LatestExchangeRatesResponse;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final ExchangeRatesClient exchangeRatesClient;

    @Value("${openexchangerates.app-id}")
    private String appId;
    @Value("${payment.commission-rate}")
    private BigDecimal commissionRate;
    @Value("${payment.default-currency}")
    private Currency defaultCurrency;
    @Value("${payment.rounding.converted-amount}")
    private int roundingConvertedAmount;
    @Value("${payment.rounding.final-amount}")
    private int roundingFinalAmount;
    @Value("${payment.verification-code.upper-limit}")
    private int upperLimitVerificationCode;
    @Value("${payment.verification-code.lower-limit}")
    private int lowerLimitVerificationCode;

    @Override
    public PaymentResponse sendPayment(PaymentRequest dto) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        BigDecimal amount = convertCurrency(dto.amount(), dto.currency(), defaultCurrency);

        String formattedSum = decimalFormat.format(dto.amount());
        int verificationCode = new Random().nextInt(lowerLimitVerificationCode, upperLimitVerificationCode);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, dto.currency().name());

        return new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                amount,
                dto.currency(),
                message
        );
    }

    @Override
    public BigDecimal convertCurrency(BigDecimal amount, Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        try {
            LatestExchangeRatesResponse rates = exchangeRatesClient.getLatestRates(appId);

            validateCurrenciesRatesExists(fromCurrency, toCurrency, rates);

            BigDecimal fromRate = rates.rates().get(fromCurrency.name());
            BigDecimal toRate = rates.rates().get(toCurrency.name());

            BigDecimal convertedAmount = amount.divide(fromRate, roundingConvertedAmount, RoundingMode.HALF_UP).multiply(toRate);

            BigDecimal commission = convertedAmount.multiply(commissionRate);
            BigDecimal finalAmount = convertedAmount.add(commission);

            log.info("Converted {} {} to {} {} (with 1% commission)", amount, fromCurrency, finalAmount, toCurrency);

            return finalAmount.setScale(roundingFinalAmount, RoundingMode.HALF_UP);

        } catch (EntityNotFoundException e) {
            log.error("Currency rate not available: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error converting currency: ", e);
            throw new DataValidationException("Failed to convert currency. " + e.getMessage());
        }
    }

    private void validateCurrenciesRatesExists(Currency fromCurrency, Currency toCurrency, LatestExchangeRatesResponse rates) {
        if (!rates.rates().containsKey(fromCurrency.name()) ||
                !rates.rates().containsKey(toCurrency.name())) {
            String errorMsg = String.format("Exchange rate not found for pair %s/%s", fromCurrency, toCurrency);
            log.error(errorMsg);
            throw new EntityNotFoundException(errorMsg);
        }
    }
}