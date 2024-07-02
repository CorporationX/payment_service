package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.CreatePaymentRequest;
import faang.school.paymentservice.dto.RedisPaymentDto;
import faang.school.paymentservice.enums.TransactionStatus;
import faang.school.paymentservice.exception.NotFoundException;
import faang.school.paymentservice.message.publisher.payment.PaymentRequestPublisher;
import faang.school.paymentservice.model.Balance;
import faang.school.paymentservice.model.BalanceAudit;
import faang.school.paymentservice.repository.BalanceAuditRepository;
import faang.school.paymentservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRequestPublisher paymentRequestPublisher;
    private final BalanceRepository balanceRepository;
    private final BalanceAuditRepository balanceAuditRepository;

    @Override
    @Transactional
    @Async(value = "paymentPool")
    public BalanceAudit createRequestForPayment(CreatePaymentRequest createPaymentRequest, long userId) {
        Balance senderBalance = findBalance(createPaymentRequest.getSenderBalanceNumber());
        Balance getterBalance = findBalance(createPaymentRequest.getGetterBalanceNumber());
        BigDecimal deposit = createPaymentRequest.getAmount();

        BalanceAudit balanceAudit = BalanceAudit.builder()
                .userId(userId)
                .senderBalance(senderBalance)
                .getterBalance(getterBalance)
                .lockValue(String.valueOf(getterBalance.getId() + senderBalance.getId()))
                .authorizationAmount(deposit)
                .actualAmount(BigDecimal.ZERO)
                .transactionStatus(TransactionStatus.PENDING)
                .auditTimestamp(LocalDateTime.now())
                .clearScheduledAt(createPaymentRequest.getClearScheduledAt())
                .currency(createPaymentRequest.getCurrency())
                .build();

        balanceAudit = balanceAuditRepository.save(balanceAudit);

        RedisPaymentDto redisPaymentDto = new RedisPaymentDto(userId, senderBalance.getId(), getterBalance.getId(),
                deposit, createPaymentRequest.getCurrency(), TransactionStatus.PENDING);

        paymentRequestPublisher.publish(redisPaymentDto);

        return balanceAudit;
    }

    @Override
    @Transactional
    @Async(value = "paymentPool")
    public BalanceAudit cancelRequestForPayment(Long balanceAuditId, long userId) {
        BalanceAudit balanceAudit = balanceAuditRepository.findById(balanceAuditId)
                .orElseThrow(() -> new NotFoundException("There is no such request in DB"));

        balanceAudit.setTransactionStatus(TransactionStatus.CANCELED);
        balanceAudit.setAuditTimestamp(LocalDateTime.now());

        balanceAudit = balanceAuditRepository.save(balanceAudit);

        RedisPaymentDto redisPaymentDto = new RedisPaymentDto(userId, balanceAudit.getSenderBalance().getId(),
                balanceAudit.getGetterBalance().getId(), balanceAudit.getAuthorizationAmount(),
                balanceAudit.getCurrency(), TransactionStatus.CANCELED);

        paymentRequestPublisher.publish(redisPaymentDto);

        return balanceAudit;
    }

    @Override
    @Transactional
    @Async(value = "paymentPool")
    public BalanceAudit forceRequestForPayment(Long balanceAuditId, long userId) {
        BalanceAudit balanceAudit = balanceAuditRepository.findById(balanceAuditId)
                .orElseThrow(() -> new NotFoundException("There is no such request in DB"));

        balanceAudit.setTransactionStatus(TransactionStatus.SUCCESS);
        balanceAudit.setAuditTimestamp(LocalDateTime.now());

        balanceAudit = balanceAuditRepository.save(balanceAudit);

        RedisPaymentDto redisPaymentDto = new RedisPaymentDto(userId, balanceAudit.getSenderBalance().getId(),
                balanceAudit.getGetterBalance().getId(), balanceAudit.getAuthorizationAmount(),
                balanceAudit.getCurrency(), TransactionStatus.SUCCESS);

        paymentRequestPublisher.publish(redisPaymentDto);

        return balanceAudit;
    }

    @Override
    @Transactional
    public void approvePendingRequests(Long limit) {
        List<BalanceAudit> pendingRequests = balanceAuditRepository.findSomeRequests(limit);

        pendingRequests.forEach(balanceAudit -> {
            balanceAudit.setTransactionStatus(TransactionStatus.SUCCESS);
            balanceAudit.setAuditTimestamp(LocalDateTime.now());

            balanceAudit = balanceAuditRepository.save(balanceAudit);

            RedisPaymentDto redisPaymentDto = new RedisPaymentDto(balanceAudit.getUserId(),
                    balanceAudit.getSenderBalance().getId(),
                    balanceAudit.getGetterBalance().getId(), balanceAudit.getAuthorizationAmount(),
                    balanceAudit.getCurrency(), TransactionStatus.SUCCESS);

            paymentRequestPublisher.publish(redisPaymentDto);
        });
    }

    private Balance findBalance(Long id) {
        return balanceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Balance with id " + id + " not found"));
    }
}

