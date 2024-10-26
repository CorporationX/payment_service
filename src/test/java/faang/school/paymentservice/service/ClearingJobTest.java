package faang.school.paymentservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.model.PendingOperation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearingJobTest {
    @Mock
    private PendingOperationService pendingOperationService;
    @InjectMocks
    private ClearingJob clearingJob;

    private PendingOperation operation;
    private PendingOperation operationTwo;

    @BeforeEach
    public void setUp() {
        operation = new PendingOperation(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "1",
                BigDecimal.ONE,
                Currency.RUB,
                OperationStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());
        operationTwo = new PendingOperation(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "2",
                BigDecimal.TEN,
                Currency.RUB,
                OperationStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    void testProcessPendingOperations_Success() {
        List<PendingOperation> operations = List.of(operation, operationTwo);
        when(pendingOperationService.getOperationsForClearing(any(LocalDateTime.class))).thenReturn(operations);

        clearingJob.processPendingOperations();

        verify(pendingOperationService, times(1)).getOperationsForClearing(any(LocalDateTime.class));
        verify(pendingOperationService, times(1)).confirmOperation(operation.getId(), false);
        verify(pendingOperationService, times(1)).confirmOperation(operationTwo.getId(), false);
    }

    @Test
    void testProcessPendingOperations_Failure() {
        List<PendingOperation> operations = List.of(operation, operationTwo);
        when(pendingOperationService.getOperationsForClearing(any(LocalDateTime.class))).thenReturn(operations);
        doThrow(new RuntimeException("Test exception")).when(pendingOperationService).confirmOperation(operation.getId(), false);

        clearingJob.processPendingOperations();

        verify(pendingOperationService, times(1)).getOperationsForClearing(any(LocalDateTime.class));
        verify(pendingOperationService, times(1)).confirmOperation(operation.getId(), false);
        verify(pendingOperationService, times(1)).sendErrorMessage(operation.getId());
        verify(pendingOperationService, times(1)).confirmOperation(operationTwo.getId(), false);
    }
}