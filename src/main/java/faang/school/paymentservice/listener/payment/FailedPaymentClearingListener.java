package faang.school.paymentservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.topics.KafkaFailedPaymentClearingResTopicProperties;
import faang.school.paymentservice.event.payment.FailedPaymentClearingEventDto;
import faang.school.paymentservice.facade.payment.PaymentOperationKafkaListenerFacade;
import faang.school.paymentservice.listener.AbstractKafkaListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FailedPaymentClearingListener extends AbstractKafkaListener<FailedPaymentClearingEventDto> {
    private final PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade;
    private final KafkaFailedPaymentClearingResTopicProperties paymentProps;

    public FailedPaymentClearingListener(ObjectMapper objectMapper,
                                         PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade,
                                         KafkaFailedPaymentClearingResTopicProperties paymentProp) {
        super(objectMapper, FailedPaymentClearingEventDto.class);
        this.paymentOperationKafkaListenerFacade = paymentOperationKafkaListenerFacade;
        this.paymentProps = paymentProp;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.failed-payment-clearing-response.name}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenPaymentAuthorizationTopic(String message) {
        FailedPaymentClearingEventDto event = getEvent(message);
        log.info("Received a message from {}: {}", paymentProps.getName(), event);
        paymentOperationKafkaListenerFacade.onClearingFailed(event);
    }
}