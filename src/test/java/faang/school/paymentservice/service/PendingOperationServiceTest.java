package faang.school.paymentservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
        operation.setIdempotencyKey("uniqueKey");
        operation.setStatus(OperationStatus.PENDING);
    }

    @Test
    void testInitiateOperation_NewOperation() {
        when(pendingOperationRepository.findByIdempotencyKey(operation.getIdempotencyKey()))
                .thenReturn(Optional.empty());

        UUID resultOperationId = pendingOperationService.initiateOperation(operation);

        assertNotNull(resultOperationId);
        verify(pendingOperationRepository, times(1)).save(operation);
        verify(pendingOperationValidator, times(1)).validateInitiateOperation(operation);
    }

    @Test
    void testInitiateOperation_ExistingOperation() {
        when(pendingOperationRepository.findByIdempotencyKey(operation.getIdempotencyKey()))
                .thenReturn(Optional.of(operation));

        UUID resultOperationId = pendingOperationService.initiateOperation(operation);

        assertEquals(operation.getId(), resultOperationId);
        verify(pendingOperationRepository, never()).save(any());
    }

    @Test
    void testCancelOperation_Success() {
        when(pendingOperationRepository.findByIdAndStatus(operationId, OperationStatus.PENDING))
                .thenReturn(Optional.of(operation));

        pendingOperationService.cancelOperation(operationId);

        assertEquals(OperationStatus.CANCELLED, operation.getStatus());
        verify(pendingOperationRepository, times(1)).save(operation);
    }

    @Test
    void testConfirmOperation_Success_Automatic() {
        when(pendingOperationRepository.findByIdAndStatus(operationId, OperationStatus.PENDING))
                .thenReturn(Optional.of(operation));

        pendingOperationService.confirmOperation(operationId, false);

        assertEquals(OperationStatus.CONFIRMED, operation.getStatus());
        verify(pendingOperationValidator, times(1)).validateAutomaticConfirmation(operation);
        verify(pendingOperationRepository, times(1)).save(operation);
    }

    @Test
    void testConfirmOperation_Success_Manual() {
        when(pendingOperationRepository.findByIdAndStatus(operationId, OperationStatus.PENDING))
                .thenReturn(Optional.of(operation));

        pendingOperationService.confirmOperation(operationId, true);

        assertEquals(OperationStatus.CONFIRMED, operation.getStatus());
        verify(pendingOperationValidator, times(1)).validateManualConfirmation(operation);
        verify(pendingOperationRepository, times(1)).save(operation);
    }

    @Test
    void testGetOperationsForClearing() {
        LocalDateTime now = LocalDateTime.now();
        PendingOperation operation1 = new PendingOperation();
        PendingOperation operation2 = new PendingOperation();

        when(pendingOperationRepository.findByStatusAndClearScheduledAtBefore(OperationStatus.PENDING, now))
                .thenReturn(List.of(operation1, operation2));

        List<PendingOperation> result = pendingOperationService.getOperationsForClearing(now);

        assertEquals(2, result.size());
        verify(pendingOperationRepository, times(1)).findByStatusAndClearScheduledAtBefore(OperationStatus.PENDING, now);
    }
}