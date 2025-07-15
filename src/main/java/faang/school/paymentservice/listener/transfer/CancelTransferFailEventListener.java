package faang.school.paymentservice.listener.transfer;

import faang.school.paymentservice.event.transfer.CancelTransferFailEventResponse;
import faang.school.paymentservice.publisher.BrokenTransferEventPublisher;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancelTransferFailEventListener extends AbstractTransferEventListener<CancelTransferFailEventResponse> {

    private final PaymentService service;

    public CancelTransferFailEventListener(
            @Value(value = "${spring.kafka.topics.transfer.consume.cancellation-failed-topic.name}")
            String topic,
            BrokenTransferEventPublisher brokenTransferEventPublisher,
            PaymentService service
            ) {
        super(topic, brokenTransferEventPublisher);
        this.service = service;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.cancellation-failed-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaCancelTransferFailEventListener"
    )
    public void listenCancelTransferFailEventResponse(CancelTransferFailEventResponse event) {
        handleEventWithValidation(event);
    }

    @Override
    public void handle(CancelTransferFailEventResponse event) {
        service.handleCancelTransferEvent(event);
    }

    @Override
    public boolean isEventValid(CancelTransferFailEventResponse event) {
        return validateObjectNonNullData(
                event,
                event::getTransferStage,
                event::getDescription,
                event::getTransactionId
        );
    }
}
