package faang.school.paymentservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.topics.KafkaFailedPaymentCancelResTopicProperties;
import faang.school.paymentservice.event.payment.FailedPaymentCancelEventDto;
import faang.school.paymentservice.facade.payment.PaymentOperationKafkaListenerFacade;
import faang.school.paymentservice.listener.AbstractKafkaListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FailedPaymentCancelListener extends AbstractKafkaListener<FailedPaymentCancelEventDto> {
    private final PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade;
    private final KafkaFailedPaymentCancelResTopicProperties paymentProps;

    public FailedPaymentCancelListener(ObjectMapper objectMapper,
                                       PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade,
                                       KafkaFailedPaymentCancelResTopicProperties paymentProp) {
        super(objectMapper, FailedPaymentCancelEventDto.class);
        this.paymentOperationKafkaListenerFacade = paymentOperationKafkaListenerFacade;
        this.paymentProps = paymentProp;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.failed-payment-cancel-response.name}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenPaymentAuthorizationTopic(String message) {
        FailedPaymentCancelEventDto event = getEvent(message);
        log.info("Received a message from {}: {}", paymentProps.getName(), event);
        paymentOperationKafkaListenerFacade.onCancellationFailed(event);
    }
}
