package faang.school.paymentservice.listener;

import faang.school.paymentservice.event.ClearingTransferFailEventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ForceClearingFailEventListener {

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.clearing-failed-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaClearTransferFailEventListener"
    )
    public void listenForceClearingFailEventResponse(ClearingTransferFailEventResponse event) {
    }
}
