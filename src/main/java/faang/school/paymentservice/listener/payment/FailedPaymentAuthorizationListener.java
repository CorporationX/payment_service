package faang.school.paymentservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.topics.KafkaSuccessPaymentAuthorizationResTopicProperties;
import faang.school.paymentservice.event.payment.FailedPaymentAuthorizationEventDto;
import faang.school.paymentservice.facade.payment.PaymentOperationKafkaListenerFacade;
import faang.school.paymentservice.listener.AbstractKafkaListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FailedPaymentAuthorizationListener extends AbstractKafkaListener<FailedPaymentAuthorizationEventDto> {
    private final PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade;
    private final KafkaSuccessPaymentAuthorizationResTopicProperties paymentProps;

    public FailedPaymentAuthorizationListener(ObjectMapper objectMapper,
                                               Class<FailedPaymentAuthorizationEventDto> eventClass,
                                               PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade,
                                               KafkaSuccessPaymentAuthorizationResTopicProperties paymentProp) {
        super(objectMapper, eventClass);
        this.paymentOperationKafkaListenerFacade = paymentOperationKafkaListenerFacade;
        this.paymentProps = paymentProp;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.payment-authorization-request.name}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenPaymentAuthorizationTopic(String message) {
        FailedPaymentAuthorizationEventDto event = getEvent(message);
        log.info("Received a message from {}: {}", paymentProps.getName(), event);
        paymentOperationKafkaListenerFacade.cancelAuthorization(event);
    }
}
