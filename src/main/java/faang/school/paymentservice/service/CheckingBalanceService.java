package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.CheckingPaymentStatusAndBalance;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.exception.ErrorOperationException;
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
public class CheckingBalanceService {
    private final PendingOperationRepository pendingOperationRepository;

    public void checkBalance(CheckingPaymentStatusAndBalance event, AccountBalanceStatus status) {
        PendingOperation pendingOperation = pendingOperationRepository.findById(event.getOperationId())
                .orElseThrow(() -> new IllegalArgumentException("Operation not found"));

        switch (status) {
            case INSUFFICIENT_FUNDS -> handleInsufficientFunds(pendingOperation, event);
            case SUFFICIENT_FUNDS -> handleSufficientFunds(pendingOperation, event);
            default -> log.warn("Unhandled balance status {} for operation {}", status, event.getOperationId());
        }
    }

    private void handleInsufficientFunds(PendingOperation operation, CheckingPaymentStatusAndBalance event) {
        if (event.getPaymentStatus() == PaymentStatus.FAILED) {
            updateOperationStatus(operation, OperationStatus.ERROR, AccountBalanceStatus.INSUFFICIENT_FUNDS);
            throw new InsufficientBalanceException("Not enough funds on the account for the requested operation.");
        }
    }

    private void handleSufficientFunds(PendingOperation operation, CheckingPaymentStatusAndBalance event) {
        switch (event.getPaymentStatus()) {
            case FAILED -> {
                updateOperationStatus(operation, OperationStatus.ERROR, AccountBalanceStatus.SUFFICIENT_FUNDS);
                throw new ErrorOperationException("Error occurred during the operation.");
            }
            case SUCCESS -> {
                updateOperationStatus(operation, OperationStatus.AUTHORIZATION, AccountBalanceStatus.SUFFICIENT_FUNDS);
                log.info("Account balance is sufficient for the operation with id: {}", operation.getId());
            }
            default -> log.warn("Unhandled payment status {} for operation {}", event.getPaymentStatus(), event.getOperationId());
        }
    }

    private void updateOperationStatus(PendingOperation operation, OperationStatus newStatus, AccountBalanceStatus balanceStatus) {
        operation.setStatus(newStatus);
        operation.setAccountBalanceStatus(balanceStatus);
        operation.setUpdatedAt(LocalDateTime.now());
        pendingOperationRepository.save(operation);
    }
}
