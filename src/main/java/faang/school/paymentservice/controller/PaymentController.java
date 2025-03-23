package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.service.CurrencyRateService;
import faang.school.paymentservice.service.PaymentService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api")
public class PaymentController {
    private final CurrencyRateService currencyRateService;
    private final PaymentMapper paymentMapper;
    private final PaymentService paymentService;

    @PostMapping("/payment/send")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest paymentRequest) {
        Payment payment = paymentMapper.paymentRequestToPayment(paymentRequest);
        payment = paymentService.createPayment(payment);
        PaymentResponse response = paymentMapper.paymentToPaymentResponse(payment);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@NotNull @RequestParam UUID paymentId) {
        Payment payment = paymentService.cancelPayment(paymentId);
        PaymentResponse response = paymentMapper.paymentToPaymentResponse(payment);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment/clearing")
    public ResponseEntity<PaymentResponse> clearingPayment(@NotNull @RequestParam UUID paymentId) {
        Payment payment = paymentService.clearingPayment(paymentId);
        PaymentResponse response = paymentMapper.paymentToPaymentResponse(payment);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment")
    public ResponseEntity<PaymentResponse> getPayment(@NotNull @RequestParam UUID paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        PaymentResponse response = paymentMapper.paymentToPaymentResponse(payment);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/currency/exchange")
    public double exchange(
            @RequestParam Currency from,
            @RequestParam Currency to,
            @NotNull @Positive @RequestParam BigDecimal amount
    ) {
        return currencyRateService.exchange(from, to, amount);
    }

    @GetMapping("/currency/time")
    public LocalDateTime getCurrenciesUpdatedAt() {
        return currencyRateService.getCurrencyRateCreatedTime();
    }
}
