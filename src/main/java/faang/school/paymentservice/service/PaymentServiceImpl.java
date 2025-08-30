package faang.school.paymentservice.service;

import faang.school.paymentservice.kafka.PaymentProducer;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.dto.PaymentMessageDto;
import faang.school.paymentservice.model.dto.PaymentRequestDto;
import faang.school.paymentservice.model.dto.PaymentResponseDto;
import faang.school.paymentservice.model.enums.PaymentStages;
import faang.school.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    @Override
    @Transactional
    public PaymentResponseDto initiatePayment(PaymentRequestDto request) {
        validateRequest(request);

        UUID idempotencyToken = UUID.randomUUID();

        Payment payment = Payment.builder()
                .idempotencyToken(idempotencyToken)
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStages.PENDING)
                .clearScheduledAt(request.getClearScheduledAt())
                .build();

        paymentRepository.save(payment);

        PaymentMessageDto message = PaymentMessageDto.builder()
                .idempotencyToken(payment.getIdempotencyToken())
                .fromAccountId(payment.getFromAccountId())
                .toAccountId(payment.getToAccountId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .scheduledAt(payment.getClearScheduledAt())
                .build();

        paymentProducer.sendAuthorization(message);

        return buildResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto cancelPayment(UUID idempotencyToken) {
        Payment payment = paymentRepository.getByIdempotencyTokenOrThrow(idempotencyToken);

        if (payment.getStatus() == PaymentStages.CANCELED || payment.getStatus() == PaymentStages.CLEARED) {
            return buildResponse(payment);
        }

        payment.setStatus(PaymentStages.CANCELED);
        paymentRepository.save(payment);

        PaymentMessageDto message = PaymentMessageDto.builder()
                .idempotencyToken(payment.getIdempotencyToken())
                .fromAccountId(payment.getFromAccountId())
                .toAccountId(payment.getToAccountId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .scheduledAt(null)
                .build();

        paymentProducer.sendCancel(message);

        return buildResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto confirmPayment(UUID idempotencyToken) {
        Payment payment = paymentRepository.getByIdempotencyTokenOrThrow(idempotencyToken);

        if (payment.getStatus() == PaymentStages.CLEARED) {
            return buildResponse(payment);
        }

        payment.setStatus(PaymentStages.CLEARED);
        paymentRepository.save(payment);

        PaymentMessageDto message = PaymentMessageDto.builder()
                .idempotencyToken(payment.getIdempotencyToken())
                .fromAccountId(payment.getFromAccountId())
                .toAccountId(payment.getToAccountId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .scheduledAt(null)
                .build();

        paymentProducer.sendClearing(message);

        return buildResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto getPaymentByIdempotencyToken(UUID idempotencyToken) {
        Payment payment = paymentRepository.getByIdempotencyTokenOrThrow(idempotencyToken);
        return buildResponse(payment);
    }

    private void validateRequest(PaymentRequestDto request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (request.getFromAccountId() == null || request.getToAccountId() == null) {
            throw new IllegalArgumentException("fromAccountId and toAccountId must be not null");
        }
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new IllegalArgumentException("fromAccountId and toAccountId cannot be the same");
        }
        if (request.getCurrency() == null) {
            throw new IllegalArgumentException("Currency must not be null");
        }
    }

    private PaymentResponseDto buildResponse(Payment payment) {
        return PaymentResponseDto.builder()
                .idempotencyToken(payment.getIdempotencyToken())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}