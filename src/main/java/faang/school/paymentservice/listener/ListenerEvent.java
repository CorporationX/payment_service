package faang.school.paymentservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.PendingDto;
import faang.school.paymentservice.service.PendingServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListenerEvent {

    private final ObjectMapper objectMapper;
    private final PendingServiceImpl pendingService;

    @KafkaListener(topics = "${spring.kafka.topics.payment-status-reset.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenMessagePendingDto(String message, Acknowledgment acknowledgment) {
        log.info("Received message: {}", message);
        try {
            PendingDto pendingDto = objectMapper.readValue(message, PendingDto.class);
            log.info("Successfully deserialized message to PendingDto: {}", pendingDto);
            pendingService.resetStatus(pendingDto);
            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize message to PendingDto: {}", message, e);
            throw new RuntimeException("Error deserializing message",e);
        }
    }
}
