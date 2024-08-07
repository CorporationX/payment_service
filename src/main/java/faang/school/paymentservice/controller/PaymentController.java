package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.OpenExchangeRatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {
    private final OpenExchangeRatesService openExchangeRatesService;

    @Value("${payment.exchangeFee}")
    private double exchangePee;

    @Value("${payment.baseCurrency}")
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
                calculateAmount(dto.amount(), dto.currency()),
                dto.currency(),
                message)
        );
    }

    private BigDecimal calculateAmount(BigDecimal amount, Currency paymentCurrency) {
        BigDecimal exchange = openExchangeRatesService.exchange(baseCurrency, paymentCurrency);

        return amount
                .multiply(exchange)
                .multiply(BigDecimal.valueOf(exchangePee));
    }
}
