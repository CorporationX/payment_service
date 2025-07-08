package faang.school.paymentservice.listener;

import faang.school.paymentservice.event.CancelTransferFailEventResponse;
import faang.school.paymentservice.event.CancelTransferSuccessEventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancelTransferFailEventListener {

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.cancellation-failed-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaCancelTransferFailEventListener"
    )
    public void listenCancelTransferFailEventResponse(CancelTransferFailEventResponse event) {
    }
}
