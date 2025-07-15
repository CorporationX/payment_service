package faang.school.paymentservice.listener.transfer;

import faang.school.paymentservice.event.transfer.CancelTransferSuccessEventResponse;
import faang.school.paymentservice.publisher.BrokenTransferEventPublisher;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancelTransferSuccessEventListener extends AbstractTransferEventListener<CancelTransferSuccessEventResponse> {

    private final PaymentService service;

    public CancelTransferSuccessEventListener(
            @Value(value = "${spring.kafka.topics.transfer.consume.cancellation-success-topic.name}")
            String topic,
            BrokenTransferEventPublisher brokenTransferEventPublisher,
            PaymentService service
    ) {
        super(topic, brokenTransferEventPublisher);
        this.service = service;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.cancellation-success-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaCancelTransferSuccessEventListener"
    )
    public void listenCancelTransferSuccessEventResponse(CancelTransferSuccessEventResponse event) {
        handleEventWithValidation(event);
    }

    @Override
    public void handle(CancelTransferSuccessEventResponse event) {
        service.handleCancelTransferEvent(event);
    }

    @Override
    public boolean isEventValid(CancelTransferSuccessEventResponse event) {
        return validateObjectNonNullData(
                event,
                event::getTransferStage,
                event::getDescription,
                event::getTransactionId
        );
    }
}
