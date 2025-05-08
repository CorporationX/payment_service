package faang.school.paymentservice.controller;


import faang.school.paymentservice.dto.PaymentOperationRequest;
import faang.school.paymentservice.service.PaymentProcessingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentProcessingController {

    private final PaymentProcessingService paymentProcessingService;

    @PostMapping
    public void initPayment(@Valid @RequestBody PaymentOperationRequest request) {
        paymentProcessingService.initPayment(request);
    }

    @PatchMapping("/cancellation/{paymentId}")
    public void cancelPayment(@PathVariable Long paymentId) {
        paymentProcessingService.cancelPayment(paymentId);
    }

    @PatchMapping("/confirmation/{paymentId}")
    public void confirmPaymentForced(@PathVariable Long paymentId) {
        paymentProcessingService.confirmPaymentForced(paymentId);
    }
}
