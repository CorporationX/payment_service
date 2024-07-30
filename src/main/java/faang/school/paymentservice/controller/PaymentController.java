package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentDto;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> doPayment(@RequestBody @Validated PaymentDto dto) {
        return ResponseEntity.ok(paymentService.doPayment(dto));
    }
}
