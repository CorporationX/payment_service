package faang.school.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.event.PaymentEvent;
import faang.school.paymentservice.mapper.OutboxMapper;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOutboxService {
    private final OutboxEventRepository outboxRepository;
    private final OutboxMapper outboxMapper;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOutboxEvent(PaymentEvent event) {
        OutboxEvent outboxEvent = outboxMapper.toOutboxEvent(event.operation());

        try {
            outboxEvent.setPayload(objectMapper.writeValueAsString(event.operation()));
            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payload for payment {}", event.operation().getId(), e);
            outboxEvent.setOutboxStatus(OutboxStatus.ERROR);
            outboxRepository.save(outboxEvent);
        }
    }
}
