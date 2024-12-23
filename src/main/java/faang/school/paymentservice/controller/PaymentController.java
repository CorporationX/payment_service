package faang.school.paymentservice.controller;

import faang.school.paymentservice.context.UserContext;
import faang.school.paymentservice.dto.PaymentRequest;
import java.text.DecimalFormat;
import java.util.Random;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.payment.AuthorizationMessage;
import faang.school.paymentservice.dto.payment.AuthorizationResponse;
import faang.school.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final UserContext userContext;

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
                dto.amount(),
                dto.currency(),
                message)
        );
    }

    @PostMapping
    public AuthorizationResponse getAuthorization(@Validated @RequestBody AuthorizationMessage message) {
        log.info("getAuthorization");
        userContext.setUserId(1L);
        return paymentService.authorizePayment(message);
    }
}
