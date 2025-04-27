package faang.school.paymentservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.paymentservice.dto.message.KafkaMessageWrapper;
import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.handler.message.OutboxEventHandler;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendEvent {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final List<OutboxEventHandler> handlers;
    private final OutboxEventRepository repository;

    @Async
    public void sendEventToKafka(OutboxEvent event) {
        try {
            OutboxEventHandler handler = getOutboxEventHandler(event);
            KafkaMessageWrapper message = handler.handle(event);
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(message.getTopic(), message.getPaymentOperationMessage());
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    event.setOutboxStatus(OutboxStatus.ERROR);
                    log.error("Failed to send event {} to Kafka: {}", event.getId(), ex.getMessage());
                } else {
                    event.setOutboxStatus(OutboxStatus.SENT);
                    log.info("Successfully sent event {} to Kafka topic {}",
                            event.getId(), result.getRecordMetadata().topic());
                }
            });
            repository.save(event);

        } catch (JsonProcessingException e) {
            event.setOutboxStatus(OutboxStatus.ERROR);
            repository.save(event);
            log.error("Failed to process event {}: {}", event.getId(), e.getMessage());
        }
    }

    private OutboxEventHandler getOutboxEventHandler(OutboxEvent event) {
        return handlers.stream()
                .filter(h -> h.canHandle(event.getEventType()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No handler found for event type: " + event.getEventType()));
    }
}
