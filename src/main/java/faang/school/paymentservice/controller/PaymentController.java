package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.payment.PaymentRequestDto;
import faang.school.paymentservice.dto.payment.PaymentResponceDto;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentMapper paymentMapper;
    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponceDto authorizePayment(@RequestBody @Valid PaymentRequestDto paymentRequestDto) {
        Payment requestPayment = paymentMapper.toPaymentEntity(paymentRequestDto);
        String accountNumberFrom = paymentRequestDto.getAccountNumberFrom();
        String accountNumberTo = paymentRequestDto.getAccountNumberTo();
        Payment responcePayment = paymentService.authorizePayment(requestPayment, accountNumberFrom, accountNumberTo);
        return paymentMapper.toPaymentResponceDto(responcePayment);
    }

    @PutMapping
    public PaymentResponceDto changePaymentStatus(@RequestParam UUID paymentId, @RequestParam PaymentStatus status) {
        Payment payment = paymentService.updatePaymentStatus(paymentId, status);
        return paymentMapper.toPaymentResponceDto(payment);
    }
}
