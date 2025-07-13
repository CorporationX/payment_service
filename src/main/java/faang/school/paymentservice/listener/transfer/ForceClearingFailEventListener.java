package faang.school.paymentservice.listener.transfer;

import faang.school.paymentservice.event.transfer.ClearingTransferFailEventResponse;
import faang.school.paymentservice.publisher.BrokenTransferEventPublisher;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ForceClearingFailEventListener extends AbstractTransferEventListener<ClearingTransferFailEventResponse> {

    private final PaymentService service;

    public ForceClearingFailEventListener(
            @Value(value = "${spring.kafka.topics.transfer.consume.clearing-failed-topic.name}")
            String topic,
            BrokenTransferEventPublisher brokenTransferEventPublisher,
            PaymentService service
    ) {
        super(topic, brokenTransferEventPublisher);
        this.service = service;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.clearing-failed-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaClearTransferFailEventListener"
    )
    public void listenForceClearingFailEventResponse(ClearingTransferFailEventResponse event) {
        handleEventWithValidation(event);
    }

    @Override
    public void handle(ClearingTransferFailEventResponse event) {
        service.handleClearTransferEvent(event);
    }

    @Override
    public boolean isEventValid(ClearingTransferFailEventResponse event) {
        return validateObjectNonNullData(
                event,
                event::getTransactionId,
                event::getDescription,
                event::getTransferStage
        );
    }
}
