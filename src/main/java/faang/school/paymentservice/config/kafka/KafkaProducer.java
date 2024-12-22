package faang.school.paymentservice.config.kafka;

import faang.school.paymentservice.dto.TestKafkaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public String send(String topic, String message) {
        kafkaTemplate.send(topic, message);
        return "Message sent";
    }
}
