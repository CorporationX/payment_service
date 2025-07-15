package faang.school.paymentservice.publisher;

import faang.school.paymentservice.event.transfer.CancelTransferEventRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CancelTransferEventPublisher extends AbstractEventPublisher<CancelTransferEventRequest>{

    public CancelTransferEventPublisher(
            @Value(value = "${spring.kafka.topics.transfer.publish.cancellation-request-topic.name}") String topic,
            KafkaTemplate<String, CancelTransferEventRequest> kafkaTemplate
    ) {
        super(topic, kafkaTemplate);
    }
}
