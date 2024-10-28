package faang.school.paymentservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import faang.school.paymentservice.exception.InsufficientBalanceException;
import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.repository.PendingOperationRepository;
import faang.school.paymentservice.validator.PendingOperationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingOperationServiceTest {
    @Mock
    private PendingOperationRepository pendingOperationRepository;
    @Mock
    private PendingOperationValidator pendingOperationValidator;
    @InjectMocks
    private PendingOperationService pendingOperationService;

    private PendingOperation operation;
    private UUID operationId;

    @BeforeEach
    void setUp() {
        operationId = UUID.randomUUID();
        operation = new PendingOperation();
        operation.setId(operationId);
        operation.setIdempotencyKey("test-key");
        operation.setAccountId(UUID.randomUUID());
        operation.setAmount(BigDecimal.valueOf(100));
        operation.setStatus(OperationStatus.PENDING);
    }

    @Test
    void testInitiateOperationSuccess() {
        doNothing().when(pendingOperationValidator).validateIdempotencyKey(operation.getIdempotencyKey());
        doNothing().when(pendingOperationValidator).validateBalance(operation.getAccountId(), operation.getAmount());

        UUID result = pendingOperationService.initiateOperation(operation);

        verify(pendingOperationValidator).validateIdempotencyKey(operation.getIdempotencyKey());
        verify(pendingOperationValidator).validateBalance(operation.getAccountId(), operation.getAmount());
        verify(pendingOperationRepository).save(operation);
        assertEquals(operationId, result);
    }

    @Test
    void testCancelOperation() {
        when(pendingOperationRepository.findByIdAndStatus(operationId, OperationStatus.PENDING))
                .thenReturn(Optional.of(operation));

        pendingOperationService.cancelOperation(operationId);

        assertEquals(OperationStatus.CANCELLED, operation.getStatus());
        verify(pendingOperationRepository).save(operation);
    }

    @Test
    void testConfirmOperationManualSuccess() {
        when(pendingOperationRepository.findByIdAndStatus(operationId, OperationStatus.PENDING))
                .thenReturn(Optional.of(operation));
        doNothing().when(pendingOperationValidator).validateManualConfirmation(operation);

        pendingOperationService.confirmOperation(operationId, true);

        verify(pendingOperationValidator).validateManualConfirmation(operation);
        verify(pendingOperationRepository).save(operation);
        assertEquals(OperationStatus.CONFIRMED, operation.getStatus());
    }

    @Test
    void testConfirmOperationAutomaticSuccess() {
        when(pendingOperationRepository.findByIdAndStatus(operationId, OperationStatus.PENDING))
                .thenReturn(Optional.of(operation));
        operation.setClearScheduledAt(LocalDateTime.now().minusMinutes(1));
        doNothing().when(pendingOperationValidator).validateAutomaticConfirmation(operation);

        pendingOperationService.confirmOperation(operationId, false);

        verify(pendingOperationValidator).validateAutomaticConfirmation(operation);
        verify(pendingOperationRepository).save(operation);
        assertEquals(OperationStatus.CONFIRMED, operation.getStatus());
    }

    @Test
    void testConfirmOperationFailsOnValidationError() {
        when(pendingOperationRepository.findByIdAndStatus(operationId, OperationStatus.PENDING))
                .thenReturn(Optional.of(operation));
        doThrow(new InsufficientBalanceException("Insufficient balance"))
                .when(pendingOperationValidator).validateAutomaticConfirmation(operation);

        pendingOperationService.confirmOperation(operationId, false);

        assertEquals(OperationStatus.FAILED, operation.getStatus());
        verify(pendingOperationRepository, times(1)).save(operation);
    }

    @Test
    void testGetOperationsForClearing() {
        LocalDateTime currentTime = LocalDateTime.now();
        pendingOperationService.getOperationsForClearing(currentTime);
        verify(pendingOperationRepository).findByStatusAndClearScheduledAtBefore(OperationStatus.PENDING, currentTime);
    }
}