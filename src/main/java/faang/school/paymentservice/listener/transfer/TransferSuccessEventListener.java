package faang.school.paymentservice.listener.transfer;

import faang.school.paymentservice.event.transfer.TransferSuccessEventResponse;
import faang.school.paymentservice.publisher.BrokenTransferEventPublisher;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferSuccessEventListener extends AbstractTransferEventListener<TransferSuccessEventResponse> {

    private final PaymentService service;

    public TransferSuccessEventListener(
            @Value(value = "${spring.kafka.topics.transfer.consume.authorization-success-topic.name}")
            String topic,
            BrokenTransferEventPublisher brokenTransferEventPublisher,
            PaymentService service
    ) {
        super(topic, brokenTransferEventPublisher);
        this.service = service;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.authorization-success-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaTransferSuccessEventListener"
    )
    public void listenTransferSuccessEventResponse(TransferSuccessEventResponse event) {
        handleEventWithValidation(event);
    }

    @Override
    public void handle(TransferSuccessEventResponse event) {
        service.handleTransferEvent(event);
    }

    @Override
    public boolean isEventValid(TransferSuccessEventResponse event) {
        return validateObjectNonNullData(
                event,
                event::getAuthorizationId,
                event::getTransferStage,
                event::getDescription,
                event::getTransactionId
        );
    }
}
