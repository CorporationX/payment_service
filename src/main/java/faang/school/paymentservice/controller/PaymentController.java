package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.CurrencyConverter;
import faang.school.paymentservice.validator.ValidatorPaymentController;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PaymentController {
    private final CurrencyConverter currencyConverter;
    private final Currency currencyOnOurAccount = Currency.RUB;
    private final ValidatorPaymentController validator;

    @Operation(description = "Service for payments")
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        validator.checkCurrency(dto.currency());
        BigDecimal finalAmount = currencyConverter.getLatestExchangeRates(dto, currencyOnOurAccount);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(finalAmount);
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, currencyOnOurAccount);

        return ResponseEntity.ok(new PaymentResponse(PaymentStatus.SUCCESS, verificationCode,
                dto.paymentNumber(), finalAmount, currencyOnOurAccount, message)
        );
    }
}
