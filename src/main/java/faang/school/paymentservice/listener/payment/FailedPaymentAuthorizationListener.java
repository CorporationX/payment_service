package faang.school.paymentservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.topics.KafkaFailedPaymentAuthorizationResTopicProperties;
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
    private final KafkaFailedPaymentAuthorizationResTopicProperties paymentProps;

    public FailedPaymentAuthorizationListener(ObjectMapper objectMapper,
                                              PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade,
                                              KafkaFailedPaymentAuthorizationResTopicProperties paymentProp) {
        super(objectMapper, FailedPaymentAuthorizationEventDto.class);
        this.paymentOperationKafkaListenerFacade = paymentOperationKafkaListenerFacade;
        this.paymentProps = paymentProp;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.failed-payment-authorization-response.name}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenPaymentAuthorizationTopic(String message) {
        FailedPaymentAuthorizationEventDto event = getEvent(message);
        log.info("Received a message from {}: {}", paymentProps.getName(), event);
        paymentOperationKafkaListenerFacade.onAuthorizationFailed(event);
    }
}
