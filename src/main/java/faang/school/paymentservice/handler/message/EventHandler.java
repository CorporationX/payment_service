package faang.school.paymentservice.handler.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.message.KafkaMessageWrapper;
import faang.school.paymentservice.model.OutboxEvent;

public interface EventHandler {
    boolean canHandle(PaymentStatus status);

    KafkaMessageWrapper handle(OutboxEvent event) throws JsonProcessingException;
}
