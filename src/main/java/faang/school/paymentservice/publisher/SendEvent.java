package faang.school.paymentservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.paymentservice.dto.message.KafkaMessageWrapper;
import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.exception.NoHandlerFoundException;
import faang.school.paymentservice.handler.message.EventHandler;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Компонент для отправки событий в Kafka.
 * <p>
 * Основные функции:
 * <ul>
 *   <li>Отправка событий платежей в соответствующие топики Kafka</li>
 *   <li>Обработка событий через соответствующие обработчики ({@link EventHandler})</li>
 *   <li>Обновление статуса событий в outbox-таблице</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SendEvent {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final List<EventHandler> handlers;
    private final OutboxEventRepository repository;

    public void sendEventToKafka(OutboxEvent event) {
        try {
            EventHandler handler = getOutboxEventHandler(event);
            KafkaMessageWrapper message = handler.handle(event);
            sendEvent(event, message);

        } catch (JsonProcessingException e) {
            event.setOutboxStatus(OutboxStatus.ERROR);
            repository.save(event);
            log.error("Failed to deserialized event {}: {}", event.getId(), e.getMessage());
        }
    }

    private void sendEvent(OutboxEvent event, KafkaMessageWrapper message) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(message.getTopic(), message.getPaymentOperationMessage());
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                event.setOutboxStatus(OutboxStatus.ERROR);
                repository.save(event);
                log.error("Failed to send event {} to Kafka: {}", event.getId(), ex.getMessage());
            } else {
                event.setOutboxStatus(OutboxStatus.SENT);
                event.setSentAt(OffsetDateTime.now());
                repository.save(event);
                log.info("Successfully sent event {} to Kafka topic {}",
                        event.getId(), result.getRecordMetadata().topic());
            }
        });
    }

    private EventHandler getOutboxEventHandler(OutboxEvent event) {
        return handlers.stream()
                .filter(h -> h.canHandle(event.getEventType()))
                .findFirst()
                .orElseThrow(() ->
                        new NoHandlerFoundException("No handler found for event type: " + event.getEventType()));
    }
}
