package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.event.PaymentEvent;
import faang.school.paymentservice.exception.EntityNotFoundException;
import faang.school.paymentservice.exception.PaymentNotAuthException;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.PaymentOperation;
import faang.school.paymentservice.repository.OutboxEventRepository;
import faang.school.paymentservice.repository.PaymentOperationRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Сервис для обработки платежных операций.
 * <p>
 * Обеспечивает основные операции с платежами:
 * <ul>
 *   <li>Инициация нового платежа</li>
 *   <li>Отмена существующего платежа</li>
 *   <li>Принудительное проведение платежа (forced payment)</li>
 * </ul>
 *
 * <p>Все операции выполняются в транзакционном контексте ({@code @Transactional}).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentOperationRepository paymentOperationRepository;
    private final PaymentMapper paymentMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final OutboxEventRepository outboxEventRepository;


    @Transactional
    public PaymentResponse initiatePayment(@NotNull @Valid PaymentRequest request) {
        PaymentOperation operation = paymentMapper.toPaymentOperation(request);

        return saveOperation(operation, PaymentStatus.PENDING);
    }

    @Transactional
    public PaymentResponse cancelPayment(@NotNull UUID id) {
        PaymentOperation authOperation = authorizationPayment(id);

        PaymentOperation operation = paymentMapper.clone(authOperation);

        return saveOperation(operation, PaymentStatus.CANCELED);
    }

    @Transactional
    public PaymentResponse forcedPayment(@NotNull UUID id) {
        PaymentOperation authOperation = authorizationPayment(id);

        PaymentOperation operation = paymentMapper.clone(authOperation);

        return saveOperation(operation, PaymentStatus.CLEARED);
    }

    private PaymentOperation authorizationPayment(UUID id) {
        PaymentOperation authOperation = paymentOperationRepository.findById(id).orElseThrow(() -> {
            log.error("Payment with id {} not found", id);
            return new EntityNotFoundException("Payment with id " + id + " not found");
        });

        if (outboxEventRepository.notExistsSentAuth(id, PaymentStatus.PENDING, OutboxStatus.SENT)) {
            log.error("Payment with id {} is not authorized", id);
            throw new PaymentNotAuthException("Payment with id " + id + " is not authorized");
        }

        authOperation.setPaymentStatus(PaymentStatus.AUTHORIZED);
        authOperation.setAuthorizationId(id);
        return authOperation;
    }

    private PaymentResponse saveOperation(PaymentOperation operation, PaymentStatus status) {
        try {
            operation.setPaymentStatus(status);
            operation = paymentOperationRepository.save(operation);

            eventPublisher.publishEvent(new PaymentEvent(operation));

            log.debug("Validation with id {} success", operation.getId());
            return paymentMapper.toPaymentResponse(operation, "");
        } catch (Exception e) {
            operation.setPaymentStatus(PaymentStatus.FAILED);
            operation = paymentOperationRepository.save(operation);

            log.debug("Validation with id {} failed", operation.getId());
            return paymentMapper.toPaymentResponse(operation, e.getMessage());
        }
    }
}
