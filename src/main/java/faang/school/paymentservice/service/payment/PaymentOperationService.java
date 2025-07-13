package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.config.job.JobClearingPaymentConfig;
import faang.school.paymentservice.entity.payment.PaymentOperation;
import faang.school.paymentservice.entity.payment.PaymentOperationStatus;
import faang.school.paymentservice.exception.payment.PaymentOperationNotFoundException;
import faang.school.paymentservice.model.payment.OperationTokenResult;
import faang.school.paymentservice.repository.payment.PaymentOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentOperationService {
    private final PaymentOperationRepository paymentOperationRepository;
    private final OperationTokenRedisService operationTokenRedisService;
    private final JobClearingPaymentConfig jobClearingPaymentConfig;
    @Transactional
    public PaymentOperation authorizePayment(PaymentOperation paymentOperation) {
        UUID redisToken = buildRedisToken(paymentOperation);

        OperationTokenResult tokenResult = operationTokenRedisService.saveOperationToken(redisToken);

        UUID operationToken = buildDbToken(paymentOperation, tokenResult.tokenModel().getCreatedAt());
        if (tokenResult.wasAlreadyPresent()) {
            // TODO: исключение
            return paymentOperationRepository.findByOperationToken(operationToken)
                    .orElseThrow();
        }

        paymentOperation.setOperationToken(operationToken);
        paymentOperation.setStatus(PaymentOperationStatus.PENDING);
        paymentOperation.setClearScheduledAt(
                tokenResult.tokenModel().getCreatedAt().
                        minusSeconds(jobClearingPaymentConfig.getScheduledAt())
        );

        PaymentOperation saved = paymentOperationRepository.save(paymentOperation);
        log.info("Payment operation {} has been saved", saved);

        return saved;
    }

    // TODO: поправить
    private UUID buildRedisToken(PaymentOperation paymentOperation) {
        String raw = paymentOperation.getAccountFromId() + ":" +
                paymentOperation.getAccountToId()   + ":" +
                paymentOperation.getCurrencyId()    + ":" +
                paymentOperation.getAmount();
        return UUID.nameUUIDFromBytes(raw.getBytes(StandardCharsets.UTF_8));
    }
    private UUID buildDbToken(PaymentOperation paymentOperation, LocalDateTime createAt) {
        String raw = buildRedisToken(paymentOperation) + ":" + createAt;
        return UUID.nameUUIDFromBytes(raw.getBytes(StandardCharsets.UTF_8));
    }

    @Transactional
    public PaymentOperation updatePaymentOperationStatus(UUID operationToken, UUID operationId, LocalDateTime timestamp,
                                                         PaymentOperationStatus status, String detail) {
        PaymentOperation paymentOperation = paymentOperationRepository.findByOperationTokenForUpdate(operationToken)
                .orElseThrow(() -> {
                    String errorMsg = String.format("Payment operation with operation token %s not found",
                            operationToken);
                    log.error(errorMsg);
                    return new PaymentOperationNotFoundException(errorMsg);
                });

        if (paymentOperation.getTimestamp() != null && timestamp.isAfter(paymentOperation.getTimestamp())) {
            return paymentOperation;
        }
        paymentOperation.setOperationId(operationId);
        paymentOperation.setStatus(status);
        paymentOperation.setDetail(detail);

        log.info("Payment operation {} has been updated", paymentOperation);

        return paymentOperation;
    }
}
