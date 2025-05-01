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

/**
 * Обработчик событий клиринга (проведения) платежей.
 * <p>
 * Реализует логику преобразования событий о проведенных платежах ({@link PaymentStatus#CLEARED})
 * в сообщения для Kafka.
 *
 * <p>Основные функции:
 * <ul>
 *   <li>Определение, может ли обработать событие по статусу платежа</li>
 *   <li>Десериализация payload из OutboxEvent</li>
 *   <li>Преобразование в DTO для Kafka ({@link PaymentOperationMessage})</li>
 *   <li>Определение топика для отправки</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class ClearingMessageHandler implements EventHandler {
    private final ObjectMapper objectMapper;
    private final PaymentMapper paymentMapper;
    private final Topics topics;

    @Override
    public boolean canHandle(PaymentStatus status) {
        return status == PaymentStatus.CLEARED;
    }

    @Override
    public KafkaMessageWrapper handle(OutboxEvent event) throws JsonProcessingException {
        String payload = event.getPayload();
        PaymentOperation operation = objectMapper.readValue(payload, PaymentOperation.class);
        PaymentOperationMessage operationMessage = paymentMapper.toClearingMessage(operation);
        String topic = topics.getClearingTopic();
        return new KafkaMessageWrapper(operationMessage, topic);
    }
}
