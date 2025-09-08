package faang.school.paymentservice.controller;

import faang.school.paymentservice.model.dto.PaymentRequestDto;
import faang.school.paymentservice.model.dto.PaymentResponseDto;
import faang.school.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentServiceController {

    private final PaymentService service;

    /**
     * Инициация платежа
     */
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDto> initiatePayment(@Valid @RequestBody PaymentRequestDto request) {
        PaymentResponseDto response = service.initiatePayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Отмена платежа по idempotencyToken
     */
    @PostMapping("/cancel/{idempotencyToken}")
    public ResponseEntity<PaymentResponseDto> cancelPayment(@PathVariable UUID idempotencyToken) {
        PaymentResponseDto response = service.cancelPayment(idempotencyToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Подтверждение (клиринг) платежа по idempotencyToken
     */
    @PostMapping("/confirm/{idempotencyToken}")
    public ResponseEntity<PaymentResponseDto> confirmPayment(@PathVariable UUID idempotencyToken) {
        PaymentResponseDto response = service.confirmPayment(idempotencyToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Получение платежа по idempotencyToken
     */
    @GetMapping("/idempotency/{idempotencyToken}")
    public ResponseEntity<PaymentResponseDto> getPaymentByIdempotencyToken(@PathVariable UUID idempotencyToken) {
        PaymentResponseDto response = service.getPaymentByIdempotencyToken(idempotencyToken);
        return ResponseEntity.ok(response);
    }
}