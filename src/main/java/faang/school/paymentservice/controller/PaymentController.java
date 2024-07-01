package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.service.PaymentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment")
    public PaymentResponse sendPayment(@RequestBody @Validated PaymentRequestDto dto) {
        return paymentService.sendPayment(dto);
    }

    @PatchMapping("/payment/{paymentId}/cancel")
    public PaymentResponse cancelPayment(@PathVariable @NotNull Long paymentId) {
        return paymentService.cancelPayment(paymentId);
    }

    @PatchMapping("/payment/{paymentId}/confirm")
    public PaymentResponse confirmPayment(@PathVariable @NotNull Long paymentId, @RequestParam(required = false) BigDecimal newAmount) {
        return paymentService.confirmPayment(paymentId, newAmount);
    }
}
