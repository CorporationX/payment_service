package faang.school.paymentservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.paymentservice.dto.payment.AuthorizationEvent;
import faang.school.paymentservice.enums.ResponseMessageStatus;
import faang.school.paymentservice.message.CancelPaymentEventHandler;
import faang.school.paymentservice.message.KafkaRecordConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaCancelPaymentEventListener {

    private final KafkaRecordConverter kafkaRecordConverter;
    private final CancelPaymentEventHandler cancelPaymentEventHandler;

    @KafkaListener(topics = "cancel-payment-topic", groupId = "my_consumer_group")
    public void consume(ConsumerRecord<String, String> record) throws JsonProcessingException{
        // Проверка на null
        if (record.value() == null) {
            log.error("Received null message from Kafka");
            return;
        }

        AuthorizationEvent event = kafkaRecordConverter.convertRecordToObject(record, AuthorizationEvent.class);

        ResponseMessageStatus responseMessageStatus = ResponseMessageStatus.CANCELLED;
        cancelPaymentEventHandler.handle(event);

    }
}
