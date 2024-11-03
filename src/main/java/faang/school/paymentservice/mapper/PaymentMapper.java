package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.payment.PaymentEventDto;
import faang.school.paymentservice.dto.payment.PaymentRequestDto;
import faang.school.paymentservice.dto.payment.PaymentResponceDto;
import faang.school.paymentservice.model.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class PaymentMapper {
    public Payment toPaymentEntity(PaymentRequestDto request) {
        return Payment.builder()
                .amount(new BigDecimal(request.getAmount()))
                .currency(request.getCurrency())
                .clearScheduledAt(request.getClearScheduledAt())
                .build();
    }

    public PaymentResponceDto toPaymentResponceDto(Payment payment) {
        return PaymentResponceDto.builder()
                .id(payment.getId())
                .amount(payment.getAmount().toString())
                .currency(payment.getCurrency())
                .accountFromId(payment.getAccountFromId())
                .accountToId(payment.getAccountToId())
                .status(payment.getStatus())
                .clearScheduledAt(payment.getClearScheduledAt())
                .build();
    }

    public PaymentEventDto toPaymentEventDto(Payment payment) {
        return PaymentEventDto.builder()
                .paymentId(payment.getId())
                .amount(payment.getAmount().toString())
                .status(payment.getStatus())
                .accountFromId(payment.getAccountFromId())
                .accountToId(payment.getAccountToId())
                .build();
    }}
