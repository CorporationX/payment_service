package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.service.ConvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {
    @Value("${exchange_rates.base}")
    private String base;
    private final ConvertService convertService;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        return ResponseEntity.ok(convertService.getConvertedPayment(dto));
    }
}
