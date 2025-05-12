package faang.school.paymentservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.event.ResponseOutboxEvent;
import faang.school.paymentservice.exception.JsonDeserializationException;
import faang.school.paymentservice.handler.ResponseOutboxEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseOutboxListener {

    private final ObjectMapper objectMapper;
    private final ResponseOutboxEventHandler eventHandler;

    @KafkaListener(
            topics = "${spring.data.kafka.topics.payment-response.name}",
            groupId = "${spring.data.kafka.consumer.group-id}"
    )
    public void receive(String message) {
        try {
            log.debug("Received new response event: {}", message);
            ResponseOutboxEvent event = objectMapper.readValue(message, ResponseOutboxEvent.class);
            eventHandler.handle(event);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializationException("Deserialization json %s to event object error", message);
        }
    }
}
