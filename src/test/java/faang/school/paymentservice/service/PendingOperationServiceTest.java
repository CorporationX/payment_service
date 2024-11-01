package faang.school.paymentservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @Mock
    private OperationMessageService operationMessageService;
    @InjectMocks
    private PendingOperationService pendingOperationService;

    private PendingOperation operation;
    private UUID operationId;

    @BeforeEach
    void setUp() {
        operationId = UUID.randomUUID();
        operation = PendingOperation.builder()
                .id(operationId)
                .idempotencyKey("test-key")
                .sourceAccountId(UUID.randomUUID())
                .targetAccountId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(100))
                .status(OperationStatus.PENDING)
                .build();
    }

    @Test
    void testInitiateOperationSuccess() {
        doNothing().when(pendingOperationValidator).validateIdempotencyKey(operation.getIdempotencyKey());

        UUID result = pendingOperationService.initiateOperation(operation);

        verify(pendingOperationValidator).validateIdempotencyKey(operation.getIdempotencyKey());
        verify(pendingOperationRepository).saveAndFlush(operation);
        verify(operationMessageService).sendOperationMessage(operation);
        assertEquals(operationId, result);
    }

    @Test
    void testCancelOperation() {
        when(pendingOperationRepository.findByIdAndStatus(operationId, OperationStatus.PENDING))
                .thenReturn(Optional.of(operation));

        pendingOperationService.cancelOperation(operationId);

        assertEquals(OperationStatus.CANCELLATION, operation.getStatus());
        verify(pendingOperationRepository).save(operation);
        verify(operationMessageService).sendOperationMessage(operation);
    }

    @Test
    void testConfirmOperationManualSuccess() {
        when(pendingOperationRepository.findByIdAndStatus(operationId, OperationStatus.PENDING))
                .thenReturn(Optional.of(operation));
        doNothing().when(pendingOperationValidator).validateManualConfirmation(operation);

        pendingOperationService.confirmOperation(operationId, true);

        verify(pendingOperationValidator).validateManualConfirmation(operation);
        verify(pendingOperationRepository).save(operation);
        verify(operationMessageService).sendOperationMessage(operation);
        assertEquals(OperationStatus.CLEARING, operation.getStatus());
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
        verify(operationMessageService).sendOperationMessage(operation);
        assertEquals(OperationStatus.CLEARING, operation.getStatus());
    }

    @Test
    void testConfirmOperationFailsOnValidationError() {
        when(pendingOperationRepository.findByIdAndStatus(operationId, OperationStatus.PENDING))
                .thenReturn(Optional.of(operation));
        doThrow(new RuntimeException("Insufficient balance"))
                .when(pendingOperationValidator).validateAutomaticConfirmation(operation);

        pendingOperationService.confirmOperation(operationId, false);

        assertEquals(OperationStatus.ERROR, operation.getStatus());
        verify(pendingOperationRepository, times(1)).save(operation);
        verify(operationMessageService).sendOperationMessage(operation);
    }

    @Test
    void testGetOperationsForClearing() {
        LocalDateTime currentTime = LocalDateTime.now();
        pendingOperationService.getOperationsForClearing(currentTime);
        verify(pendingOperationRepository).findByStatusAndClearScheduledAtBefore(OperationStatus.PENDING, currentTime);
    }
}