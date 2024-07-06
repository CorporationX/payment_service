package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.*;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody @Validated InvoiceDto invoiceDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.create(invoiceDto));
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.cancel(id));
    }

    @PostMapping("/clear/{id}")
    public ResponseEntity<PaymentResponse> clearPayment(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.clear(id));
    }

    @PostMapping("/success/{id}")
    public ResponseEntity<PaymentResponse> passPayment(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.success(id));
    }
}