package faang.school.paymentservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.paymentservice.dto.payment.AuthorizationEvent;
import faang.school.paymentservice.message.KafkaRecordConverter;
import faang.school.paymentservice.message.SuccessClearingEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSuccessClearingEventListener {
    private final SuccessClearingEventHandler successClearingEventHandler;
    private final KafkaRecordConverter kafkaRecordConverter;

    @KafkaListener(topics = "successful-payment-clear-topic", groupId = "successful-payment-clear-topic")
    public void consume(ConsumerRecord<String, String> record) throws JsonProcessingException {
        // Проверка на null
        if (record.value() == null) {
            log.error("Received null message from Kafka");
            return;
        }

        // Извлечение значения из ConsumerRecord
        AuthorizationEvent event = kafkaRecordConverter.convertRecordToObject(record, AuthorizationEvent.class);
        // Здесь можно добавить логику какую либо
        successClearingEventHandler.handle(event);
    }
}
