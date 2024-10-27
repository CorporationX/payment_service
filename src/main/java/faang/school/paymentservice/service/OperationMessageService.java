package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.OperationMessage;
import faang.school.paymentservice.model.OperationType;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.publisher.EventPublisher;
import faang.school.paymentservice.repository.PendingOperationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class OperationMessageService {
    private final EventPublisher<OperationMessage> eventPublisher;
    private final PendingOperationRepository pendingOperationRepository;

    @Autowired
    public OperationMessageService(EventPublisher<OperationMessage> eventPublisher,
                                   PendingOperationRepository pendingOperationRepository) {
        this.eventPublisher = eventPublisher;
        this.pendingOperationRepository = pendingOperationRepository;
    }

    public void sendOperationMessage(UUID operationId, OperationType operationType) {
        log.debug("Sending operation message for operationId: {}, operationType: {}", operationId, operationType);

        PendingOperation pendingOperation = pendingOperationRepository.findById(operationId)
                .orElseThrow(() -> new RuntimeException("Operation not found with ID: " + operationId));

        OperationMessage message = OperationMessage.builder()
                .operationId(pendingOperation.getId())
                .accountId(pendingOperation.getAccountId())
                .idempotencyKey(pendingOperation.getIdempotencyKey())
                .amount(pendingOperation.getAmount())
                .currency(pendingOperation.getCurrency())
                .operationType(operationType)
                .build();

        eventPublisher.publish(message);
        log.info("Operation message sent: {}", message);
    }
}
