package faang.school.paymentservice.service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.service.publisher.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public abstract class KafkaAbstractRequestListener {
    public static final String FAILED_PARSE_OBJECT = "Failed to parse object";
    private final ObjectMapper objectMapper;
    private final KafkaEventPublisher kafkaEventPublisher;

    protected void handleRequest(String message, Consumer<PaymentRequest> consumer) {
        try {
            PaymentRequest paymentRequest = objectMapper.readValue(message, PaymentRequest.class);
            log.info("we got message");
            consumer.accept(paymentRequest);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse message: {}. Error: {}", message, e.getMessage());
            throw new RuntimeException(FAILED_PARSE_OBJECT, e);
        }
    }

    protected void publishRequest(String topic, PaymentResponse paymentResponse) {
        kafkaEventPublisher.publishEvent(topic, paymentResponse);
    }
}
