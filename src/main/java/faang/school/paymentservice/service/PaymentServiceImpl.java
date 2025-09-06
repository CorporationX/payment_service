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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;
    private final PaymentMapper mapper;

    @Override
    @Transactional
    public PaymentResponseDto initiatePayment(PaymentRequestDto request) {
        Payment payment = mapper.toPayment(request);
        paymentRepository.save(payment);
        log.info("Создан платеж idempotencyToken={}: {} → {}, сумма={}, валюта={}",
                payment.getIdempotencyToken(),
                payment.getFromAccountId(),
                payment.getToAccountId(),
                payment.getAmount(),
                payment.getCurrency());

        sendPaymentMessage(payment, PaymentMessageType.AUTHORIZATION, payment.getClearScheduledAt());
        log.info("Отправлено сообщение AUTHORIZATION для idempotencyToken={}", payment.getIdempotencyToken());

        return mapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto cancelPayment(UUID idempotencyToken) {
        Payment payment = paymentRepository.getByIdempotencyTokenOrThrow(idempotencyToken);
        log.info("Попытка отмены платежа idempotencyToken={}", idempotencyToken);

        if (payment.getStatus() == PaymentStages.CANCELED || payment.getStatus() == PaymentStages.CLEARED) {
            log.info("Платеж уже в статусе {}. Отмена не требуется.", payment.getStatus().getDescription());
            return mapper.toResponse(payment);
        }

        payment.setStatus(PaymentStages.CANCELED);
        paymentRepository.save(payment);
        sendPaymentMessage(payment, PaymentMessageType.CANCEL, null);
        log.info("Платеж отменен и отправлено сообщение CANCEL для idempotencyToken={}", idempotencyToken);

        return mapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto confirmPayment(UUID idempotencyToken) {
        Payment payment = paymentRepository.getByIdempotencyTokenOrThrow(idempotencyToken);
        log.info("Попытка подтверждения платежа idempotencyToken={}", idempotencyToken);

        if (payment.getStatus() == PaymentStages.CLEARED) {
            log.info("Платеж уже подтвержден.");
            return mapper.toResponse(payment);
        }

        payment.setStatus(PaymentStages.CLEARED);
        paymentRepository.save(payment);
        sendPaymentMessage(payment, PaymentMessageType.CLEARING, null);
        log.info("Платеж подтвержден и отправлено сообщение CLEARING для idempotencyToken={}", idempotencyToken);

        return mapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto getPaymentByIdempotencyToken(UUID idempotencyToken) {
        Payment payment = paymentRepository.getByIdempotencyTokenOrThrow(idempotencyToken);
        log.info("Получен платеж idempotencyToken={}, статус={}", idempotencyToken, payment.getStatus().getDescription());
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
            default -> throw new IllegalArgumentException("Неизвестный тип сообщения: " + type);
        }
    }
}