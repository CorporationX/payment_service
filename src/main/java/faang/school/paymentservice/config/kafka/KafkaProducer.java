package faang.school.paymentservice.config.kafka;

import faang.school.paymentservice.dto.TestKafkaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public String send(String topic, Object message) {
        kafkaTemplate.send(topic, message);
        return "Message sent";
    }
}
