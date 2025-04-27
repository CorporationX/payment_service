package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(
            @RequestBody @NotNull @Valid PaymentRequest request) {
         paymentService.initiatePayment(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("{id}/cancel")
    public ResponseEntity<Void> cancelPayment(@PathVariable UUID id) {
        paymentService.cancelPayment(id);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("{id}/forced-payment")
    public ResponseEntity<Void> forcedPayment(@PathVariable UUID id) {
        paymentService.forcedPayment(id);
        return ResponseEntity.accepted().build();
    }

//    @PostMapping("/payment")
//    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
//        DecimalFormat decimalFormat = new DecimalFormat("0.00");
//        String formattedSum = decimalFormat.format(dto.amount());
//        int verificationCode = new Random().nextInt(1000, 10000);
//        String message = String.format("Dear friend! Thank you for your purchase! " +
//                        "Your payment on %s %s was accepted.",
//                formattedSum, dto.currency().name());
//
//        return ResponseEntity.ok(new PaymentResponse(
//                PaymentStatus.SUCCESS,
//                verificationCode,
//                dto.paymentNumber(),
//                dto.amount(),
//                dto.currency(),
//                message)
//        );
//    }
}
