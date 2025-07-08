package faang.school.paymentservice.listener;

import faang.school.paymentservice.event.CancelTransferSuccessEventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancelTransferSuccessEventListener {

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.cancellation-success-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaCancelTransferSuccessEventListener"
    )
    public void listenCancelTransferSuccessEventResponse(CancelTransferSuccessEventResponse event) {
    }
}
