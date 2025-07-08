package faang.school.paymentservice.listener;

import faang.school.paymentservice.event.TransferEventRequest;
import faang.school.paymentservice.event.TransferFailEventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferFailEventListener {

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.authorization-failed-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaTransferFailEventListener"
    )
    public void listenTransferFailEventResponse(TransferFailEventResponse event) {
    }
}
