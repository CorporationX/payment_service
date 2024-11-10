package faang.school.paymentservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.model.AccountBalanceStatus;
import faang.school.paymentservice.model.Category;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearingJobTest {
    @Mock
    private PendingOperationService pendingOperationService;
    @Mock
    private OperationMessageService operationMessageService;
    @InjectMocks
    private ClearingJob clearingJob;

    private PendingOperation operation;
    private PendingOperation operationTwo;

    @BeforeEach
    public void setUp() {
        operation = PendingOperation.builder()
                .id(UUID.randomUUID())
                .sourceAccountId(UUID.randomUUID())
                .targetAccountId(UUID.randomUUID())
                .idempotencyKey("1")
                .amount(BigDecimal.ONE)
                .currency(Currency.RUB)
                .status(OperationStatus.PENDING)
                .category(Category.OTHER)
                .accountBalanceStatus(AccountBalanceStatus.SUFFICIENT_FUNDS)
                .clearScheduledAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        operationTwo = PendingOperation.builder()
                .id(UUID.randomUUID())
                .sourceAccountId(UUID.randomUUID())
                .targetAccountId(UUID.randomUUID())
                .idempotencyKey("2")
                .amount(BigDecimal.TEN)
                .currency(Currency.RUB)
                .status(OperationStatus.PENDING)
                .category(Category.OTHER)
                .accountBalanceStatus(AccountBalanceStatus.SUFFICIENT_FUNDS)
                .clearScheduledAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testProcessPendingOperations_Success() {
        List<PendingOperation> operations = List.of(operation, operationTwo);
        when(pendingOperationService.getOperationsForClearing(any(LocalDateTime.class))).thenReturn(operations);

        clearingJob.processPendingOperations();

        verify(pendingOperationService, times(1)).getOperationsForClearing(any(LocalDateTime.class));
        verify(pendingOperationService, times(1)).confirmOperation(operation.getId(), false);
        verify(pendingOperationService, times(1)).confirmOperation(operationTwo.getId(), false);
        verifyNoInteractions(operationMessageService);
    }

    @Test
    void testProcessPendingOperations_Failure() {
        List<PendingOperation> operations = List.of(operation, operationTwo);
        when(pendingOperationService.getOperationsForClearing(any(LocalDateTime.class))).thenReturn(operations);
        doThrow(new RuntimeException("Test exception"))
                .when(pendingOperationService).confirmOperation(operation.getId(), false);

        clearingJob.processPendingOperations();

        verify(pendingOperationService,
                times(1)).getOperationsForClearing(any(LocalDateTime.class));
        verify(pendingOperationService,
                times(1)).confirmOperation(operation.getId(), false);
        verify(pendingOperationService,
                times(1)).confirmOperation(operationTwo.getId(), false);
        verify(operationMessageService,
                times(1)).sendOperationMessage(operation);
        verifyNoMoreInteractions(operationMessageService);
    }
}