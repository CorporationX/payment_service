package faang.school.paymentservice.publisher;

import faang.school.paymentservice.event.TransferEventRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransferEventPublisher extends AbstractEventPublisher<TransferEventRequest>{

    public TransferEventPublisher(
            @Value(value = "${spring.kafka.topics.authorization-request-topic.name}") String topic,
            KafkaTemplate<String, TransferEventRequest> kafkaTemplate
    ) {
        super(topic, kafkaTemplate);
    }
}
