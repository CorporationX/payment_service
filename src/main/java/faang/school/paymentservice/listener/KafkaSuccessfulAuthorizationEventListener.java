package faang.school.paymentservice.listener;

import faang.school.paymentservice.dto.payment.AuthorizationEvent;
import faang.school.paymentservice.message.SuccessAuthEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSuccessfulAuthorizationEventListener {
    private final SuccessAuthEventHandler successAuthEventHandler;

    @KafkaListener(topics = "successful-payment-auth-topic", groupId = "my_consumer_group")
    public void consume(ConsumerRecord<String, AuthorizationEvent> record) {
        // Извлечение значения из ConsumerRecord
        AuthorizationEvent event = record.value();
        // Обработка полученного события
        System.out.println("Received successful payment authorization event: " + event);
        // Здесь можно добавить логику какую либо
        // добавит handler для обработки успешных платежей
        successAuthEventHandler.handle(event);
        log.info("Successfully processed clearing payment event: {}", event);
    }
}
