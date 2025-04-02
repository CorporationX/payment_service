package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final CurrencyConverterService converter;

    public PaymentResponse sendPayment(PaymentRequest dto){
        BigDecimal finalAmount = converter.convert(dto.amount(),dto.currency(), Currency.USD);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(finalAmount);
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, Currency.USD.name());

        return new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                finalAmount,
                Currency.USD,
                message);
    }
}
