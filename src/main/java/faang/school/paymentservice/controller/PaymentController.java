package faang.school.paymentservice.controller;

import faang.school.paymentservice.config.ExchangeCurrencyProperties;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeCurrencyResponse;
import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.PaymentService;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.DecimalMin;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PaymentController {
    private final PaymentService paymentService;
    private final ExchangeCurrencyProperties currencyProperties;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        String formattedSum = decimalFormat(dto.amount());

        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, dto.currency().name());

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                getVerificationCode(),
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                message)
        );
    }

    @GetMapping("/exchange")
    public ResponseEntity<ExchangeCurrencyResponse> convertCurrency(
            @RequestParam @DecimalMin(value = "0.01") BigDecimal amount,
            @RequestParam(required = false, defaultValue = "${currency.exchange.base}") Currency currencyFrom,
            @RequestParam @ValidCurrency Currency currencyTo) {

        BigDecimal convertedAmount = paymentService.convert(amount, currencyFrom, currencyTo);

        String message = String.format(
                "Your amount %s %s converted to %s %s, commission %s%%",
                decimalFormat(amount), currencyFrom,
                decimalFormat(convertedAmount), currencyTo,
                decimalFormat(currencyProperties.commission()));

        return ResponseEntity.ok(new ExchangeCurrencyResponse(
                getVerificationCode(),
                amount, currencyFrom,
                convertedAmount, currencyTo,
                message)
        );
    }

    private String decimalFormat(BigDecimal amount) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(amount);
    }

    private int getVerificationCode() {
        return new Random().nextInt(1000, 10000);
    }
}
