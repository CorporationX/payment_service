package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.payment.PaymentRequest;
import faang.school.paymentservice.service.OrderService;
import faang.school.paymentservice.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final Map<String, PaymentService> paymentServices;

    @PostMapping("/stripe/payment")
    public void retrieveStripePayment(
            @Valid @RequestBody PaymentRequest dto,
            @RequestHeader("x-secret") String secretKey
    ) {
        paymentServices.get("stripe").processPayment(dto, secretKey);
    }

    @PostMapping("/crypto/payment")
    public void retrieveCryptoPayment(
            @Valid @RequestBody PaymentRequest dto,
            @RequestHeader("x-secret") String secretKey
    ) {
        paymentServices.get("crypto").processPayment(dto, secretKey);
    }
}
