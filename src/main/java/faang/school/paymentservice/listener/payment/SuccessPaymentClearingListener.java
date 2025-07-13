package faang.school.paymentservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.topics.KafkaSuccessPaymentClearingResTopicProperties;
import faang.school.paymentservice.event.payment.SuccessPaymentClearingEventDto;
import faang.school.paymentservice.facade.payment.PaymentOperationKafkaListenerFacade;
import faang.school.paymentservice.listener.AbstractKafkaListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SuccessPaymentClearingListener extends AbstractKafkaListener<SuccessPaymentClearingEventDto> {
    private final PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade;
    private final KafkaSuccessPaymentClearingResTopicProperties paymentProps;

    public SuccessPaymentClearingListener(ObjectMapper objectMapper,
                                          PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade,
                                          KafkaSuccessPaymentClearingResTopicProperties paymentProp) {
        super(objectMapper, SuccessPaymentClearingEventDto.class);
        this.paymentOperationKafkaListenerFacade = paymentOperationKafkaListenerFacade;
        this.paymentProps = paymentProp;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.success-payment-clearing-response.name}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenPaymentAuthorizationTopic(String message) {
        SuccessPaymentClearingEventDto event = getEvent(message);
        log.info("Received a message from {}: {}", paymentProps.getName(), event);
        paymentOperationKafkaListenerFacade.onClearingCompleted(event);
    }
}
