package faang.school.paymentservice.listener.transfer;

import faang.school.paymentservice.event.transfer.TransferFailEventResponse;
import faang.school.paymentservice.publisher.BrokenTransferEventPublisher;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferFailEventListener extends AbstractTransferEventListener<TransferFailEventResponse> {

    private final PaymentService service;

    public TransferFailEventListener(
            @Value(value = "${spring.kafka.topics.transfer.consume.authorization-failed-topic.name}")
            String topic,
            BrokenTransferEventPublisher brokenTransferEventPublisher,
            PaymentService service
    ) {
        super(topic, brokenTransferEventPublisher);
        this.service = service;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.authorization-failed-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaTransferFailEventListener"
    )
    public void listenTransferFailEventResponse(TransferFailEventResponse event) {
        handleEventWithValidation(event);
    }

    @Override
    public void handle(TransferFailEventResponse event) {
        service.handleTransferEvent(event);
    }

    @Override
    public boolean isEventValid(TransferFailEventResponse event) {
        return validateObjectNonNullData(
                event,
                event::getAuthorizationId,
                event::getTransferStage,
                event::getDescription,
                event::getTransactionId
        );
    }
}
