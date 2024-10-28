package faang.school.paymentservice.validator;

import faang.school.paymentservice.client.AccountServiceClient;
import faang.school.paymentservice.dto.BalanceResponseDto;
import faang.school.paymentservice.exception.InsufficientBalanceException;
import faang.school.paymentservice.exception.InvalidOperationException;
import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.repository.PendingOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class PendingOperationValidator {
    private final AccountServiceClient accountServiceClient;
    private final PendingOperationRepository pendingOperationRepository;

    public void validateIdempotencyKey(String idempotencyKey) {
        Optional<PendingOperation> existingOperation = pendingOperationRepository.findByIdempotencyKey(idempotencyKey);
        if (existingOperation.isPresent()) {
            throw new InvalidOperationException("Operation with the same idempotency key already exists");
        }
    }
    public void validateBalance(UUID accountId, BigDecimal amount) {
        BalanceResponseDto balance = accountServiceClient.findBalanceByAccountId(accountId);

        BigDecimal availableBalance = balance.currentBalance().subtract(balance.authBalance());
        if (availableBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for account ID: " + accountId);
        }
    }
    public void validateManualConfirmation(PendingOperation operation) {
        if (operation.getStatus() != OperationStatus.PENDING) {
            throw new InvalidOperationException("Operation cannot be manually confirmed as it is not in PENDING status");
        }
    }
    public void validateAutomaticConfirmation(PendingOperation operation) {
        if (operation.getStatus() != OperationStatus.PENDING) {
            throw new InvalidOperationException("Operation cannot be automatically confirmed as it is not in PENDING status");
        }
        if (operation.getClearScheduledAt().isAfter(LocalDateTime.now())) {
            throw new InvalidOperationException("Cannot automatically confirm operation before scheduled time");
        }
    }
}
