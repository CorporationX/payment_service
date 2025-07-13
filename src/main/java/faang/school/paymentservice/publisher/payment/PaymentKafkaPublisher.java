package faang.school.paymentservice.publisher.payment;

import faang.school.paymentservice.config.kafka.topics.KafkaPaymentCancelReqTopicProperties;
import faang.school.paymentservice.config.kafka.topics.KafkaPaymentClearingReqTopicProperties;
import faang.school.paymentservice.event.payment.PaymentAuthorizationEventDto;
import faang.school.paymentservice.config.kafka.topics.KafkaPaymentAuthorizationReqTopicProperties;
import faang.school.paymentservice.event.payment.PaymentCancelEventDto;
import faang.school.paymentservice.event.payment.PaymentClearingEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentKafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaPaymentAuthorizationReqTopicProperties authorizationProps;
    private final KafkaPaymentClearingReqTopicProperties clearingProps;
    private final KafkaPaymentCancelReqTopicProperties cancelProps;

    public void sendMessage(PaymentAuthorizationEventDto event) {
        kafkaTemplate.send(authorizationProps.getName(), event).thenAccept(result ->
                        log.info("PaymentAuthorization event {} sent to Kafka topic {}",
                                event, authorizationProps.getName()))
                .exceptionally(ex -> {
                    log.error("Failed to send PaymentAuthorization event to Kafka topic '{}'. Error: {}",
                            authorizationProps.getName(), ex.getMessage());
                    return null;
                });
    }

    public void sendMessage(PaymentClearingEventDto event) {
        kafkaTemplate.send(clearingProps.getName(), event).thenAccept(result ->
                        log.info("PaymentClearing event {} sent to Kafka topic {}",
                                event, clearingProps.getName()))
                .exceptionally(ex -> {
                    log.error("Failed to send PaymentClearing event to Kafka topic '{}'. Error: {}",
                            clearingProps.getName(), ex.getMessage());
                    return null;
                });
    }

    public void sendMessage(PaymentCancelEventDto event) {
        kafkaTemplate.send(cancelProps.getName(), event).thenAccept(result ->
                        log.info("PaymentCancel event {} sent to Kafka topic {}",
                                event, cancelProps.getName()))
                .exceptionally(ex -> {
                    log.error("Failed to send PaymentCancel event to Kafka topic '{}'. Error: {}",
                            cancelProps.getName(), ex.getMessage());
                    return null;
                });
    }
}

