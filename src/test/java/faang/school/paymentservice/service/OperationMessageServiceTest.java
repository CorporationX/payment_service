package faang.school.paymentservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.OperationMessage;
import faang.school.paymentservice.model.OperationType;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.publisher.EventPublisher;
import faang.school.paymentservice.repository.PendingOperationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OperationMessageServiceTest {
    @Mock
    private EventPublisher<OperationMessage> eventPublisher;
    @Mock
    private PendingOperationRepository pendingOperationRepository;
    @InjectMocks
    private OperationMessageService operationMessageService;

    @Test
    void testSendOperationMessage_Success() {
        UUID operationId = UUID.randomUUID();
        OperationType operationType = OperationType.AUTHORIZATION;

        PendingOperation pendingOperation = new PendingOperation();
        pendingOperation.setId(operationId);
        pendingOperation.setAccountId(UUID.randomUUID());
        pendingOperation.setIdempotencyKey(UUID.randomUUID().toString());
        pendingOperation.setAmount(BigDecimal.TEN);
        pendingOperation.setCurrency(Currency.RUB);

        when(pendingOperationRepository.findById(operationId)).thenReturn(Optional.of(pendingOperation));

        operationMessageService.sendOperationMessage(operationId, operationType);

        verify(pendingOperationRepository, times(1)).findById(operationId);
        verify(eventPublisher, times(1)).publish(any(OperationMessage.class));
    }

    @Test
    void testSendOperationMessage_OperationNotFound() {
        UUID operationId = UUID.randomUUID();
        OperationType operationType = OperationType.AUTHORIZATION;

        when(pendingOperationRepository.findById(operationId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                operationMessageService.sendOperationMessage(operationId, operationType)
        );

        verify(pendingOperationRepository, times(1)).findById(operationId);
        verify(eventPublisher, never()).publish(any(OperationMessage.class));
    }
}