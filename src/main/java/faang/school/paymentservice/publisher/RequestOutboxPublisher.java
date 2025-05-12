package faang.school.paymentservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.properties.kafka.PaymentOperationTopicProperties;
import faang.school.paymentservice.dto.event.RequestOutboxEvent;
import faang.school.paymentservice.exception.JsonSerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestOutboxPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentOperationTopicProperties properties;

    public void publish(RequestOutboxEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(properties.name(), message);
            log.debug("Published payment request event {} on kafka topic {}", event.toString(), properties.name());
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException("Serialization object %s in json error", event.toString());
        }
    }
}
