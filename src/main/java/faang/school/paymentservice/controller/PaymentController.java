package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentDto;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.service.currency.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentMapper paymentMapper;
    private final PaymentService paymentService;

    @PostMapping("/authorize")
    public PaymentDto authorizePayment(@RequestBody @Valid PaymentDto paymentDto) {
        Payment requestPayment = paymentMapper.toPaymentEntity(paymentDto);
        Payment responcePayment = paymentService.authorizePayment(requestPayment);
        return paymentMapper.toPaymentDto(responcePayment);

    }



}
