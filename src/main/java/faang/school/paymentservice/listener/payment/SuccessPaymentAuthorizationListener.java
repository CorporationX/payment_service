package faang.school.paymentservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.topics.KafkaSuccessPaymentAuthorizationResTopicProperties;
import faang.school.paymentservice.event.payment.SuccessPaymentAuthorizationEventDto;
import faang.school.paymentservice.facade.payment.PaymentOperationKafkaListenerFacade;
import faang.school.paymentservice.listener.AbstractKafkaListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SuccessPaymentAuthorizationListener extends AbstractKafkaListener<SuccessPaymentAuthorizationEventDto> {
    private final PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade;
    private final KafkaSuccessPaymentAuthorizationResTopicProperties paymentProps;

    public SuccessPaymentAuthorizationListener(ObjectMapper objectMapper,
                                               PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade,
                                               KafkaSuccessPaymentAuthorizationResTopicProperties paymentProp) {
        super(objectMapper, SuccessPaymentAuthorizationEventDto.class);
        this.paymentOperationKafkaListenerFacade = paymentOperationKafkaListenerFacade;
        this.paymentProps = paymentProp;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.success-payment-authorization-response.name}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenPaymentAuthorizationTopic(String message) {
        SuccessPaymentAuthorizationEventDto event = getEvent(message);
        log.info("Received a message from {}: {}", paymentProps.getName(), event);
        paymentOperationKafkaListenerFacade.onAuthorizationCompleted(event);
    }
}