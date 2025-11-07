package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRatesDto;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final ExchangeRatesClient exchangeRatesClient;

    @Override
    public ResponseEntity<PaymentResponse> sendPayment(PaymentRequest paymentRequest) {
        Currency requiredCurrency = Currency.USD;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(paymentRequest.amount());
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, paymentRequest.currency().name());
        double currencyConversionFee = 0.01;
        try {
            ExchangeRatesDto exchangeRatesDto = exchangeRatesClient.getRates(paymentRequest.currency().toString());
            double rate = exchangeRatesDto.rates().get(paymentRequest.currency().toString());
            double amountInRequiredCurrency = paymentRequest.amount().toBigInteger().doubleValue() / rate;
            double amountAfterCurrencyConversion =
                    amountInRequiredCurrency - amountInRequiredCurrency * currencyConversionFee;
            return ResponseEntity.ok(new PaymentResponse(
                    PaymentStatus.SUCCESS,
                    verificationCode,
                    paymentRequest.paymentNumber(),
                    new BigDecimal(amountAfterCurrencyConversion),
                    requiredCurrency,
                    message)
            );
        } catch (Exception e) {
            log.error("Попытка оплатить валютой c обозначением {}.", paymentRequest.currency());
            throw new RuntimeException("Оплата данной валютой не принимается.");
        }
    }
}
