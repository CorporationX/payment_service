package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.PaymentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/payment")
    public PaymentResponse sendPayment(@RequestBody @Validated PaymentRequest dto) {
    return paymentService.createNewPayment(dto);
    }

    @GetMapping("/status")
    public PaymentStatus getStatus(@RequestParam @NotNull @NotBlank String idempotencyToken){
        return paymentService.getStatus(idempotencyToken);
    }

    @PostMapping("/cleaning/status")
    public PaymentResponse getStatus(@RequestParam @NotNull @NotBlank String idempotencyToken, @RequestParam PaymentStatus paymentStatus){
        return paymentService.cleanPayment(idempotencyToken,paymentStatus);
    }

}
