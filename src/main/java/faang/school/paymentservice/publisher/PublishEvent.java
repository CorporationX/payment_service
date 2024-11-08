package faang.school.paymentservice.publisher;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.KafkaConfig;
import faang.school.paymentservice.config.kafka.KafkaProperties;
import faang.school.paymentservice.dto.PendingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PublishEvent {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaProperties properties;

    public void publishMessageAuthorization(PendingDto message) {
        kafkaTemplate.executeInTransaction(operations -> {
            try {
                String messageString = objectMapper.writeValueAsString(message);
                operations.send("payment-authorization", messageString);
                log.info("Successfully published message to payment-authorization topic: {}", messageString);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize message for payment-authorization topic", e);
                throw new RuntimeException("Error serializing message", e);
            }
            return true;
        });
    }

    public void publishMessageClear(List<PendingDto> message) {
        kafkaTemplate.executeInTransaction(operations -> {
            try {
                String messageString = objectMapper.writeValueAsString(message);
                operations.send("payment-clear", messageString);
                log.info("Successfully published message to payment-clear topic: {}", messageString);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize message for payment-clear topic", e);
                throw new RuntimeException("Error serializing message", e);
            }
            return true;
        });
    }
}
