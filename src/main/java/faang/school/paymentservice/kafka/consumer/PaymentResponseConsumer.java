package faang.school.paymentservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.service.PaymentService;
import faang.school.paymentservice.util.JsonUtil;
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
public class PaymentResponseConsumer {
    private final PaymentService paymentService;
    private final JsonUtil jsonUtil;

    @KafkaListener(topics = "${kafka-topic.consumer.payment-response}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handle(Message<String> message, Acknowledgment acknowledgment) {
        String key = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class);
        log.info("Received message key: {}", key);
        PaymentResponse paymentResponse = mapToPaymentResponse(message.getPayload());
        paymentService.savePaymentData(paymentResponse);
        acknowledgment.acknowledge();
    }

    private PaymentResponse mapToPaymentResponse(String data) {
        try {
            return jsonUtil.mapToObject(data, PaymentResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed parsing payment response object", e);
            throw new RuntimeException(e);
        }
    }
}
