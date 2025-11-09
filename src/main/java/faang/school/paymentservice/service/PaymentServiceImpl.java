package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final CurrencyConverterService converterService;
    @Value("${app.target-currency}")
    private String targetCurrency;
    @Value("${app.verification-code.min}")
    private int verificationCodeMin;
    @Value("${app.verification-code.max}")
    private int verificationCodeMax;

    @Override
    public PaymentResponse processPayment(PaymentRequest dto) {
        BigDecimal convertedAmount = converterService.convertToRub(dto.currency().name(), dto.amount());

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(convertedAmount);

        int verificationCode = new Random().nextInt(verificationCodeMin, verificationCodeMax);
        String message = String.format(
                "Dear friend! Thank you for your purchase! Your payment on %s RUB was accepted."
                        .formatted(formattedSum)
        );

        return new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                convertedAmount,
                Currency.valueOf(targetCurrency),
                message
        );
    }
}
