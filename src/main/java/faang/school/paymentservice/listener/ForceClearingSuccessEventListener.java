package faang.school.paymentservice.listener;

import faang.school.paymentservice.event.ClearingTransferFailEventResponse;
import faang.school.paymentservice.event.ClearingTransferSuccessEventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ForceClearingSuccessEventListener {

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.clearing-success-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaClearTransferSuccessEventListener"
    )
    public void listenForceClearingSuccessEventResponse(ClearingTransferSuccessEventResponse event) {
    }
}
