package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.CheckingAccountBalance;
import faang.school.paymentservice.exception.InsufficientBalanceException;
import faang.school.paymentservice.model.AccountBalanceStatus;
import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.repository.PendingOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class CheckingAccountBalanceService {
    private final PendingOperationRepository pendingOperationRepository;

    public void checkBalance(CheckingAccountBalance event, AccountBalanceStatus status) {
        PendingOperation pendingOperation = pendingOperationRepository.findById(event.getOperationId())
                .orElseThrow(() -> new IllegalArgumentException("Operation not found"));

        if (status == AccountBalanceStatus.INSUFFICIENT_FUNDS) {
            pendingOperation.setAccountBalanceStatus(AccountBalanceStatus.INSUFFICIENT_FUNDS);
            pendingOperation.setStatus(OperationStatus.ERROR);
            pendingOperation.setUpdatedAt(LocalDateTime.now());
            pendingOperationRepository.save(pendingOperation);
            throw new InsufficientBalanceException("Not enough funds on the account for the requested operation.");
        } else {
            pendingOperation.setAccountBalanceStatus(AccountBalanceStatus.SUFFICIENT_FUNDS);
            pendingOperation.setStatus(OperationStatus.AUTHORIZATION);
            pendingOperation.setUpdatedAt(LocalDateTime.now());
            pendingOperationRepository.save(pendingOperation);
            log.info("Account balance is sufficient for the operation with id: {}", event.getOperationId());
        }
    }
}
