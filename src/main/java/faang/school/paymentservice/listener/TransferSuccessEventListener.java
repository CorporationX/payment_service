package faang.school.paymentservice.listener;

import faang.school.paymentservice.event.TransferFailEventResponse;
import faang.school.paymentservice.event.TransferSuccessEventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferSuccessEventListener {

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.authorization-success-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaTransferSuccessEventListener"
    )
    public void listenTransferSuccessEventResponse(TransferSuccessEventResponse event) {
    }
}
