package faang.school.paymentservice.service;

import faang.school.paymentservice.annotations.SendPendingOperationMessage;
import faang.school.paymentservice.exception.OperationNotFoundException;
import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.model.OperationType;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.repository.PendingOperationRepository;
import faang.school.paymentservice.validator.PendingOperationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PendingOperationService {
    private final PendingOperationRepository pendingOperationRepository;
    private final PendingOperationValidator pendingOperationValidator;

    @SendPendingOperationMessage(OperationType.AUTHORIZATION)
    @Transactional
    public UUID initiateOperation(PendingOperation operation) {
        pendingOperationValidator.validateInitiateOperation(operation);

        Optional<PendingOperation> existingOperation = pendingOperationRepository.findByIdempotencyKey(operation.getIdempotencyKey());
        if (existingOperation.isPresent()) {
            return existingOperation.get().getId();
        }

        pendingOperationRepository.save(operation);
        log.info("Operation initiated with ID: {}", operation.getId());
        return operation.getId();
    }

    @SendPendingOperationMessage(OperationType.CANCELLATION)
    @Transactional
    public void cancelOperation(UUID operationId) {
        PendingOperation operation = getOperationForProcessing(operationId);
        updateOperationStatus(operation, OperationStatus.CANCELLED);
        log.info("Operation canceled with ID: {}", operationId);
    }

    @SendPendingOperationMessage(OperationType.CLEARING)
    @Transactional
    public void confirmOperation(UUID operationId, boolean isManual) {
        PendingOperation operation = getOperationForProcessing(operationId);
        try {
            if (isManual) {
                pendingOperationValidator.validateManualConfirmation(operation);
            } else {
                pendingOperationValidator.validateAutomaticConfirmation(operation);
            }
            updateOperationStatus(operation, OperationStatus.CONFIRMED);
            log.info("Operation confirmed with ID: {}", operationId);
        } catch (Exception e) {
            log.error("Error during operation confirmation for ID {}: {}", operationId, e.getMessage(), e);
            updateOperationStatus(operation, OperationStatus.FAILED);
            sendErrorMessage(operationId);
        }
    }

    @SendPendingOperationMessage(OperationType.ERROR)
    public void sendErrorMessage(UUID operationId) {
        log.info("Sending error message for operation ID: {}", operationId);
    }

    private void updateOperationStatus(PendingOperation operation, OperationStatus status) {
        operation.setStatus(status);
        operation.setUpdatedAt(LocalDateTime.now());
        pendingOperationRepository.save(operation);
    }

    public List<PendingOperation> getOperationsForClearing(LocalDateTime currentTime) {
        return pendingOperationRepository.findByStatusAndClearScheduledAtBefore(OperationStatus.PENDING, currentTime);
    }

    private PendingOperation getOperationForProcessing(UUID operationId) {
        return pendingOperationRepository.findByIdAndStatus(operationId, OperationStatus.PENDING)
                .orElseThrow(() -> new OperationNotFoundException("Operation not found or in invalid status"));
    }
}