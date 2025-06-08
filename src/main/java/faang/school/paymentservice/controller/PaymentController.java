package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.ExchangeResponseDto;
import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {
    private final PaymentServiceImpl paymentService;
    private static final BigDecimal PERCENT = BigDecimal.valueOf(1.01);

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        BigDecimal percentConverter = dto.amount().multiply(PERCENT);
        BigDecimal converter = paymentService.converter(percentConverter, dto.fromCurrency().name(),
                dto.toCurrency().name());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(dto.amount());
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, dto.fromCurrency().name());

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                converter,
                dto.fromCurrency(),
                message)
        );
    }

    @GetMapping
    public ResponseEntity<ExchangeResponseDto> getAllCurrency() {
        return ResponseEntity.ok().body(paymentService.getLatestRates());
    }
}
