package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    @Value("${payment.commission.rate}")
    private BigDecimal commissionRate;
    private final ExchangeRatesService exchangeRatesService;

    public PaymentResponse sendPayment(PaymentRequest request) {
        Currency paymentCurrency = request.paymentCurrency();
        Currency targetCurrency = request.targetCurrency();
        BigDecimal amount = request.amount();

        int verificationCode = new Random().nextInt(1000, 10000);

        BigDecimal rate = exchangeRatesService.getExchangeRate(paymentCurrency, targetCurrency);

        BigDecimal convertedSum = amount
                .multiply(rate)
                .multiply(BigDecimal.ONE.subtract(commissionRate))
                .setScale(2, RoundingMode.HALF_UP);

        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %.2f %s was accepted.",
                convertedSum, targetCurrency.name());

        return new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                request.paymentNumber(),
                convertedSum,
                request.targetCurrency(),
                message
        );
    }
}
