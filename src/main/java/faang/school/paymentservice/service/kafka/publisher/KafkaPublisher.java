package faang.school.paymentservice.service.kafka.publisher;

import faang.school.paymentservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaPublisher {
    private final JsonUtils jsonUtils;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendInTransaction(Object object, String topic, String correlationHeader, String correlationId) {
        kafkaTemplate.executeInTransaction(kafkaOperations -> {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, jsonUtils.serialize(object));
            if (correlationId != null) {
                record.headers().add(new RecordHeader(correlationHeader, correlationId.getBytes(StandardCharsets.UTF_8)));
            }
            kafkaOperations.send(record);
            log.info("Published to Kafka: {} with correlationId: {}", object, correlationId);
            return true;
        });
    }
}
