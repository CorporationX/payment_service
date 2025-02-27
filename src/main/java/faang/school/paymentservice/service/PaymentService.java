package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.ExchangeRatesResponse;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final ExchangeRateService exchangeRateService;

    public PaymentResponse sendPayment(PaymentRequest request) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(request.amount());
        int verificationCode = new Random().nextInt(1000, 10000);
        ExchangeRatesResponse exchangeRates = exchangeRateService.getLatestExchangeRates(request.current(), request.target());
        double rate = exchangeRates.getRates().get(request.target());


        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, request.current().name());


        return new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                request.paymentNumber(),
                request.amount(),
                request.target(),
                message
        );
    }
}
