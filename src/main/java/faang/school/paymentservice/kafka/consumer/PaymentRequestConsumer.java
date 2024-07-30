package faang.school.paymentservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.service.PaymentRequestService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author Evgenii Malkov
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentRequestConsumer {
    private final PaymentRequestService requestService;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "${kafka-topic.consumer.payment-request}",
                    groupId = "${spring.kafka.consumer.group-id}")
    public void handle(Message<String> message, Acknowledgment acknowledgment) {
        String key = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class);
        log.info("Received message key: {}", key);
        PaymentRequest paymentRequest = parsePaymentRequest(message.getPayload(), key);
        if (paymentRequest == null) {
            acknowledgment.acknowledge();
            return;
        }
        requestService.savePaymentRequest(paymentRequest);
        acknowledgment.acknowledge();
    }

    @Nullable
    private PaymentRequest parsePaymentRequest(String payload, String key) {
        try {
            return mapper.readValue(payload, PaymentRequest.class);
        } catch (JsonProcessingException e) {
            log.error(String.format("Failed parse payload to PaymentRequest. Key: %s", key), e);
            return null;
        }
    }
}
