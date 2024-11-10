package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.payment.PaymentUpdateDto;
import faang.school.paymentservice.dto.payment.PaymentCreateDto;
import faang.school.paymentservice.dto.payment.PaymentDto;
import faang.school.paymentservice.service.payment.PaymentOperationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentOperationService paymentOperationService;

    @PostMapping
    public PaymentDto sendPayment(@RequestBody @Valid PaymentCreateDto dto) {
        return paymentOperationService.sendPayment(dto);
    }

    @PatchMapping("/{paymentId}")
    public PaymentDto updatePayment(@PathVariable @NonNull UUID paymentId,
                                    @RequestBody @Valid PaymentUpdateDto paymentUpdateRequestDto) {
        return switch (paymentUpdateRequestDto.getAction()) {
            case CONFIRM -> paymentOperationService.confirmPayment(paymentId);
            case CANCEL -> paymentOperationService.cancelPayment(paymentId);
        };
    }
}
