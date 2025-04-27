package faang.school.paymentservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.message.AuthorizationMessage;
import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.model.PaymentOperation;
import faang.school.paymentservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final PaymentMapper paymentMapper;

    @Value("${publisher.batch}")
    private int batch;

    public void publishEvent() {
        Pageable pageable = PageRequest.of(0, batch);
        List<OutboxEvent> newEvents = outboxEventRepository.findByOutboxStatus(OutboxStatus.NEW, pageable);
//надо ассинхронно
        for (OutboxEvent event : newEvents) {
            sendEventToKafka(event);
        }
    }

    public void sendEventToKafka(OutboxEvent event) {
        try {
            String payload = event.getPayload();
            PaymentOperation operation = objectMapper.readValue(payload, PaymentOperation.class);
            AuthorizationMessage message = paymentMapper.toAuthorizationMessage(operation);
            kafkaTemplate.send("your-kafka-topic", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
