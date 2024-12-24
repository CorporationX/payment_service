package faang.school.paymentservice.listener;

import faang.school.paymentservice.dto.payment.AuthorizationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSuccessfulAuthorizationEventListener {

    @KafkaListener(topics = "successful-payment-auth-topic", groupId = "my_consumer_group")
    public void consume(ConsumerRecord<String, AuthorizationEvent> record) {
        // Извлечение значения из ConsumerRecord
        AuthorizationEvent event = record.value();
        // Обработка полученного события
        System.out.println("Received successful payment authorization event: " + event);
        // Здесь можно добавить логику какую либо

    }
}
