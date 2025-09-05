package faang.school.paymentservice.service;

import faang.school.paymentservice.kafka.PaymentProducer;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.dto.PaymentMessageDto;
import faang.school.paymentservice.model.dto.PaymentRequestDto;
import faang.school.paymentservice.model.dto.PaymentResponseDto;
import faang.school.paymentservice.model.enums.PaymentMessageType;
import faang.school.paymentservice.model.enums.PaymentStages;
import faang.school.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;
    @Qualifier("paymentMapper")
    private final PaymentMapper mapper;

    @Override
    @Transactional
    public PaymentResponseDto initiatePayment(PaymentRequestDto request) {

        Payment payment = mapper.toPayment(request);
        paymentRepository.save(payment);

        sendPaymentMessage(payment, PaymentMessageType.AUTHORIZATION, payment.getClearScheduledAt());

        return mapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto cancelPayment(UUID idempotencyToken) {
        Payment payment = paymentRepository.getByIdempotencyTokenOrThrow(idempotencyToken);

        if (payment.getStatus() == PaymentStages.CANCELED || payment.getStatus() == PaymentStages.CLEARED) {
            return mapper.toResponse(payment);
        }

        payment.setStatus(PaymentStages.CANCELED);
        paymentRepository.save(payment);

        sendPaymentMessage(payment, PaymentMessageType.CANCEL, null);

        return mapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto confirmPayment(UUID idempotencyToken) {
        Payment payment = paymentRepository.getByIdempotencyTokenOrThrow(idempotencyToken);

        if (payment.getStatus() == PaymentStages.CLEARED) {
            return mapper.toResponse(payment);
        }

        payment.setStatus(PaymentStages.CLEARED);
        paymentRepository.save(payment);

        sendPaymentMessage(payment, PaymentMessageType.CLEARING, null);

        return mapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto getPaymentByIdempotencyToken(UUID idempotencyToken) {
        Payment payment = paymentRepository.getByIdempotencyTokenOrThrow(idempotencyToken);
        return mapper.toResponse(payment);
    }

    private void sendPaymentMessage(Payment payment, PaymentMessageType type, LocalDateTime scheduledAt) {
        PaymentMessageDto message = PaymentMessageDto.builder()
                .idempotencyToken(payment.getIdempotencyToken())
                .fromAccountId(payment.getFromAccountId())
                .toAccountId(payment.getToAccountId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .scheduledAt(scheduledAt)
                .type(type)
                .build();

        switch (type) {
            case AUTHORIZATION -> paymentProducer.sendAuthorization(message);
            case CANCEL -> paymentProducer.sendCancel(message);
            case CLEARING -> paymentProducer.sendClearing(message);
        }
    }
}