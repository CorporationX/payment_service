package faang.school.paymentservice.service;

import faang.school.paymentservice.config.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestKafkaService {
    private final KafkaProducer kafkaProducer;

    public String send(String topic, String message) {
        kafkaProducer.send(topic, message);
        return "Message sent";
    }
}
