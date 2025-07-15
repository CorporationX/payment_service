package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.config.job.JobClearingPaymentConfig;
import faang.school.paymentservice.entity.payment.PaymentOperation;
import faang.school.paymentservice.entity.payment.PaymentOperationStatus;
import faang.school.paymentservice.exception.payment.PaymentOperationNotFoundException;
import faang.school.paymentservice.model.payment.OperationTokenModel;
import faang.school.paymentservice.repository.payment.PaymentOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentOperationService {
    private final PaymentOperationRepository paymentOperationRepository;
    private final OperationTokenRedisService operationTokenRedisService;
    private final JobClearingPaymentConfig jobClearingPaymentConfig;
    private final OperationTokenFactory operationTokenFactory;

    @Transactional(readOnly = true)
    public PaymentOperation getPaymentOperationById(UUID paymentOperationId) {
        return paymentOperationRepository.findById(paymentOperationId)
                .orElseThrow(() -> {
                    String errorMsg = String.format("Payment operation with id %s not found",
                            paymentOperationId);
                    log.error(errorMsg);
                    return new PaymentOperationNotFoundException(errorMsg);
                });
    }
    @Transactional
    public PaymentOperation authorizePayment(PaymentOperation paymentOperation) {
        UUID redisToken = operationTokenFactory.buildRedisToken(paymentOperation);

        OperationTokenModel tokenRedis = operationTokenRedisService.saveOperationToken(redisToken);

        UUID operationToken = operationTokenFactory.buildDbToken(paymentOperation, tokenRedis.getCreatedAt());
        if (tokenRedis.isWasAlreadyPresent()) {
            PaymentOperation payment = paymentOperationRepository.findByOperationToken(operationToken)
                    .orElseThrow(() -> {
                        String errorMsg = String.format("Payment operation with operation token %s not found",
                                operationToken);
                        log.error(errorMsg);
                        return new PaymentOperationNotFoundException(errorMsg);
                    });
            payment.setWasAlreadyPresent(true);
            return payment;
        }

        paymentOperation.setOperationToken(operationToken);
        paymentOperation.setStatus(PaymentOperationStatus.PENDING);
        paymentOperation.setClearScheduledAt(
                tokenRedis.getCreatedAt().minusSeconds(jobClearingPaymentConfig.getScheduledAt())
        );

        PaymentOperation saved = paymentOperationRepository.save(paymentOperation);
        log.info("Payment operation {} has been saved", saved);

        return saved;
    }

    @Transactional
    public PaymentOperation cancelClearing(UUID paymentOperationId) {
        PaymentOperation paymentOperation = paymentOperationRepository.findByIdForUpdate(paymentOperationId)
                .orElseThrow(() -> {
                    String errorMsg = String.format("Payment operation with id %s not found",
                            paymentOperationId);
                    log.error(errorMsg);
                    return new PaymentOperationNotFoundException(errorMsg);
                });

        paymentOperation.setClearScheduledAt(null);
        log.info("Payment operation {} has been updated", paymentOperation);

        return paymentOperation;
    }

    @Transactional
    public void updatePaymentOperationStatus(UUID operationToken, LocalDateTime timestamp,
                                                         PaymentOperationStatus status, String detail) {
        PaymentOperation paymentOperation = paymentOperationRepository.findByOperationTokenForUpdate(operationToken)
                .orElseThrow(() -> {
                    String errorMsg = String.format("Payment operation with operation token %s not found",
                            operationToken);
                    log.error(errorMsg);
                    return new PaymentOperationNotFoundException(errorMsg);
                });

        if (paymentOperation.getTimestamp() != null && timestamp.isAfter(paymentOperation.getTimestamp())) {
            return;
        }
        paymentOperation.setStatus(status);
        paymentOperation.setDetail(detail);

        log.info("Payment operation {} has been updated", paymentOperation);
    }

    @Transactional
    public Optional<PaymentOperation> getOperationForClearing() {
        Optional<PaymentOperation> paymentOperation = paymentOperationRepository.findOperationReadyToClear();

        paymentOperation.ifPresent(operation -> cancelClearing(operation.getId()));

        return paymentOperation;
    }
}
