package faang.school.paymentservice.handler.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.Topics;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.message.KafkaMessageWrapper;
import faang.school.paymentservice.dto.message.PaymentOperationMessage;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.model.PaymentOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationMessageHandler implements EventHandler {
    private final ObjectMapper objectMapper;
    private final PaymentMapper paymentMapper;
    private final Topics topics;

    @Override
    public boolean canHandle(PaymentStatus status) {
        return status == PaymentStatus.PENDING;
    }

    @Override
    public KafkaMessageWrapper handle(OutboxEvent event) throws JsonProcessingException {
        String payload = event.getPayload();
        PaymentOperation operation = objectMapper.readValue(payload, PaymentOperation.class);
        PaymentOperationMessage operationMessage = paymentMapper.toAuthorizationMessage(operation);
        String topic = topics.getAuthorizationTopic();
        return new KafkaMessageWrapper(operationMessage, topic);
    }
}
