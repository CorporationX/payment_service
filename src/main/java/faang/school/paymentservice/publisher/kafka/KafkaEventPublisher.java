package faang.school.paymentservice.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.OperationMessage;
import faang.school.paymentservice.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "kafka")
public class KafkaEventPublisher implements EventPublisher<OperationMessage> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper,
                               @Value("${spring.kafka.topic.pending_operation}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OperationMessage event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, json);
            log.info("Event published to Kafka topic {}: {}", topic, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
