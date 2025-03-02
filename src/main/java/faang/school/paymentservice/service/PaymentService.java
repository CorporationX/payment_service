package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.ExchangeRatesResponse;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final double TRANSFER_FEE = 0.01;

    private final ExchangeRateService exchangeRateService;

    public PaymentResponse sendPayment(PaymentRequest request) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(request.amount());
        int verificationCode = new Random().nextInt(1000, 10000);
        ExchangeRatesResponse exchangeRates = exchangeRateService.getLatestExchangeRates(request.current(), request.target());
        BigDecimal rate = exchangeRates.getRates().get(request.target());

        BigDecimal convertedSum = request.amount().multiply(rate).multiply(new BigDecimal("0.99")).round(new MathContext);

        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, request.current().name());


        return new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                request.paymentNumber(),
                convertedSum,
                request.target(),
                message
        );
    }
}
