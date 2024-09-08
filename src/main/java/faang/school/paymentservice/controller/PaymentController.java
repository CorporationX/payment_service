package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.rates.CurrencyConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final CurrencyConverter currencyConverter;

    @Value("${convector.base_currency}")
    private String baseCurrency;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        BigDecimal amountAfterConvert = currencyConverter.calculateConversion(dto.currency(), dto.amount());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(dto.amount());
        String formattedAmountAfterConvert = decimalFormat.format(amountAfterConvert);
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted. " + "Including comic convector completed %s %s",
                formattedSum, dto.currency().name(), formattedAmountAfterConvert, baseCurrency);

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                message)
        );
    }
}
