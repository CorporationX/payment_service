package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "Operations with payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(
            summary = "Initiate payment",
            description = "Creates a new payment transaction"
    )
    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(
            @RequestBody @NotNull @Valid PaymentRequest request) {
        log.info("Initiating payment for request: {}", request);

        PaymentResponse response = paymentService.initiatePayment(request);
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Cancel payment",
            description = "Cancels existing payment by ID"
    )
    @PostMapping("{id}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@NotNull @PathVariable UUID id) {
        log.info("Cancelling payment with id: {}", id);
        PaymentResponse response = paymentService.cancelPayment(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Force payment",
            description = "Executes forced payment operation (admin only)"
    )
    @PostMapping("{id}/forced-payment")
    public ResponseEntity<PaymentResponse> forcedPayment(@NotNull @PathVariable UUID id) {
        log.info("Executing forced payment for id: {}", id);
        PaymentResponse response = paymentService.forcedPayment(id);
        return ResponseEntity.ok(response);
    }
}
