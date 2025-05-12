package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentOperationRequest;
import faang.school.paymentservice.service.PaymentProcessingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentProcessingController {

    private final PaymentProcessingService paymentProcessingService;

    @PostMapping
    public void initPayment(@Valid @RequestBody PaymentOperationRequest request) {
        paymentProcessingService.initPayment(request);
    }

    @PatchMapping("/cancellation/{token}")
    public void cancelPayment(@PathVariable UUID token) {
        paymentProcessingService.cancelPayment(token);
    }

    @PatchMapping("/confirmation/{token}")
    public void confirmPaymentForced(@PathVariable UUID token) {
        paymentProcessingService.confirmPaymentForced(token);
    }

    @GetMapping("/{token}")
    public void getPaymentByToken(@PathVariable UUID token) {
        paymentProcessingService.getPaymentByToken(token);
    }
}
