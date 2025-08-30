package faang.school.paymentservice.kafka;

import faang.school.paymentservice.model.dto.PaymentMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentMessageDto> kafkaTemplate;

    @Value("${app.kafka.topics.authorization}")
    private String authorizationTopic;

    @Value("${app.kafka.topics.cancel}")
    private String cancelTopic;

    @Value("${app.kafka.topics.clearing}")
    private String clearingTopic;

    @Async
    public void sendAuthorization(PaymentMessageDto message) {
        sendMessage(authorizationTopic, message);
    }

    @Async
    public void sendCancel(PaymentMessageDto message) {
        sendMessage(cancelTopic, message);
    }

    @Async
    public void sendClearing(PaymentMessageDto message) {
        sendMessage(clearingTopic, message);
    }

    private void sendMessage(String topic, PaymentMessageDto message) {
        try {
            kafkaTemplate.send(topic, message.getIdempotencyToken().toString(), message);
            log.info("Message sent successfully to topic {}: {}", topic, message);
        } catch (Exception ex) {
            log.error("Failed to send message to topic {}: {}. Error: {}", topic, message, ex.getMessage(), ex);
        }
    }
}