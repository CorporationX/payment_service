package faang.school.paymentservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.topics.KafkaSuccessPaymentCancelResTopicProperties;
import faang.school.paymentservice.event.payment.SuccessPaymentCancelEventDto;
import faang.school.paymentservice.facade.payment.PaymentOperationKafkaListenerFacade;
import faang.school.paymentservice.listener.AbstractKafkaListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SuccessPaymentCancelListener extends AbstractKafkaListener<SuccessPaymentCancelEventDto> {
    private final PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade;
    private final KafkaSuccessPaymentCancelResTopicProperties paymentProps;

    public SuccessPaymentCancelListener(ObjectMapper objectMapper,
                                        PaymentOperationKafkaListenerFacade paymentOperationKafkaListenerFacade,
                                        KafkaSuccessPaymentCancelResTopicProperties paymentProp) {
        super(objectMapper, SuccessPaymentCancelEventDto.class);
        this.paymentOperationKafkaListenerFacade = paymentOperationKafkaListenerFacade;
        this.paymentProps = paymentProp;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.success-payment-cancel-response.name}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenPaymentAuthorizationTopic(String message) {
        SuccessPaymentCancelEventDto event = getEvent(message);
        log.info("Received a message from {}: {}", paymentProps.getName(), event);
        paymentOperationKafkaListenerFacade.onCancellationCompleted(event);
    }
}