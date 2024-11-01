package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.OperationMessage;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class OperationMessageService {
    private final EventPublisher<OperationMessage> eventPublisher;

    public void sendOperationMessage(PendingOperation pendingOperation) {
        log.debug("Sending operation message for operations id: {}", pendingOperation.getId());

        OperationMessage message = OperationMessage.builder()
                .operationId(pendingOperation.getId())
                .sourceAccountId(pendingOperation.getSourceAccountId())
                .targetAccountId(pendingOperation.getTargetAccountId())
                .idempotencyKey(pendingOperation.getIdempotencyKey())
                .amount(pendingOperation.getAmount())
                .currency(pendingOperation.getCurrency())
                .category(pendingOperation.getCategory())
                .status(pendingOperation.getStatus())
                .build();

        eventPublisher.publish(message);
        log.info("Operation message sent: {}", message);
    }
}
