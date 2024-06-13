package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.services.OpenExchangeRatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final OpenExchangeRatesService openExchangeRatesService;

    @Value("${payment.exchange_fee}")
    private double exchangeFee;
    @Value("${currency.base_currency}")
    private Currency baseCurrency;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(dto.amount());
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, dto.currency().name());

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                //dto.amount(),
                calculateAmount(dto.amount(), dto.currency()),
                dto.currency(),
                message)
        );
    }

    private BigDecimal calculateAmount(BigDecimal amount, Currency paymentCurrency) {
        BigDecimal exchangeRate = openExchangeRatesService.exchange(baseCurrency, paymentCurrency);
        return amount
                .multiply(exchangeRate)
                .multiply((BigDecimal.valueOf(exchangeFee)));
    }
}
