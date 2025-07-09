package faang.school.paymentservice.publisher.payment;

import faang.school.paymentservice.event.payment.PaymentAuthorizationEventDto;
import faang.school.paymentservice.kafka.topics.KafkaPaymentAuthorizationReqTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentKafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaPaymentAuthorizationReqTopicProperties successProps;

    public void sendMessage(PaymentAuthorizationEventDto event) {
        kafkaTemplate.send(successProps.getName(), event).thenAccept(result ->
                        log.info("SuccessPaymentAuthorization event {} sent to Kafka topic {}",
                                event, successProps.getName()))
                .exceptionally(ex -> {
                    log.error("Failed to send SuccessPaymentAuthorization event to Kafka topic '{}'. Error: {}",
                            successProps.getName(), ex.getMessage());
                    return null;
                });
    }
}

