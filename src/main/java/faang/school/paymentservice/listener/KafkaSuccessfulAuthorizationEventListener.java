package faang.school.paymentservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.paymentservice.dto.payment.AuthorizationEvent;
import faang.school.paymentservice.enums.ResponseMessageStatus;
import faang.school.paymentservice.message.KafkaRecordConverter;
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
    private final KafkaRecordConverter kafkaRecordConverter;

    @KafkaListener(topics = "successful-payment-auth-topic", groupId = "my_consumer_group")
    public void consume(ConsumerRecord<String, String> record) throws JsonProcessingException {
        // Извлечение значения из ConsumerRecord
        // Обработка полученного события
        System.out.println("Received successful payment authorization event: " + record.value());
        AuthorizationEvent event = kafkaRecordConverter.convertRecordToObject(record, AuthorizationEvent.class);
        // Здесь можно добавить логику какую либо
        // добавит handler для обработки успешных платежей
        ResponseMessageStatus responseMessageStatus = ResponseMessageStatus.OK;
        successAuthEventHandler.handle(event);
        log.info("Successfully processed clearing payment event: {}", event);
    }
}
