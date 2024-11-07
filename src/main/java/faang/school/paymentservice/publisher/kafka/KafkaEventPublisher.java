package faang.school.paymentservice.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.OperationMessage;
import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "kafka")
public class KafkaEventPublisher implements EventPublisher<OperationMessage> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<OperationStatus, String> topicMap;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper,
                               @Value("${spring.kafka.topic.auth-payment-request}") String authTopic,
                               @Value("${spring.kafka.topic.cancel-payment-request}") String cancelTopic,
                               @Value("${spring.kafka.topic.clearing-payment-request}") String clearingTopic,
                               @Value("${spring.kafka.topic.error-payment-request}") String errorChannel) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topicMap = Map.of(
                OperationStatus.PENDING, authTopic,
                OperationStatus.CANCELLATION, cancelTopic,
                OperationStatus.CLEARING, clearingTopic,
                OperationStatus.ERROR, errorChannel
        );
    }

    @Override
    public void publish(OperationMessage event) {
        String topic = topicMap.get(event.getStatus());
        if (topic == null) {
            throw new IllegalArgumentException("No topic configured for status: " + event.getStatus());
        }

        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, json);
            log.info("Event published to Kafka topic {}: {}", topic, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}