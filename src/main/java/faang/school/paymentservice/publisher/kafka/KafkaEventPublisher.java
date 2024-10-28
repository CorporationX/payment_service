package faang.school.paymentservice.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.KafkaConfig;
import faang.school.paymentservice.dto.OperationMessage;
import faang.school.paymentservice.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("kafka")
public class KafkaEventPublisher implements EventPublisher<OperationMessage> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;
    private final KafkaAdmin kafkaAdmin;
    private final KafkaConfig kafkaConfig;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper,
                               @Value("${spring.kafka.topic.pending_operation}") String topic,
                               KafkaAdmin kafkaAdmin,
                               KafkaConfig kafkaConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.kafkaAdmin = kafkaAdmin;
        this.kafkaConfig = kafkaConfig;
        this.objectMapper = objectMapper;
        createTopicIfNotExists();
    }

    private void createTopicIfNotExists() {
        NewTopic newTopic = kafkaConfig.createNewTopic(topic, 1, (short) 1);

        try {
            kafkaAdmin.createOrModifyTopics(newTopic);
            log.info("Kafka topic '{}' checked or created successfully", topic);
        } catch (Exception e) {
            log.error("Failed to create topic '{}': {}", topic, e.getMessage());
        }
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
