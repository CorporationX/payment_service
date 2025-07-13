package faang.school.paymentservice.publisher;

import faang.school.paymentservice.event.transfer.ClearingTransferEventRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ForceClearingEventPublisher extends AbstractEventPublisher<ClearingTransferEventRequest>{

    public ForceClearingEventPublisher(
            @Value(value = "${spring.kafka.topics.transfer.publish.clear-request-topic.name}") String topic,
            KafkaTemplate<String, ClearingTransferEventRequest> kafkaTemplate
    ) {
        super(topic, kafkaTemplate);
    }
}
