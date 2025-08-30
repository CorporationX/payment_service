package faang.school.paymentservice.kafka;

import faang.school.paymentservice.model.dto.PaymentMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Сервис для отправки сообщений о платежах в Kafka.
 * <p>
 * Обеспечивает публикацию событий платежей на три отдельных топика:
 * <ul>
 *     <li>{@link #authorizationTopic} — события авторизации платежа;</li>
 *     <li>{@link #cancelTopic} — события отмены платежа;</li>
 *     <li>{@link #clearingTopic} — события проведения платежа (clearing).</li>
 * </ul>
 * <p>
 * Каждое сообщение {@link PaymentMessageDto} отправляется с ключом, равным {@code idempotencyToken},
 * что обеспечивает идемпотентную обработку на стороне потребителей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentMessageDto> kafkaTemplate;

    /** Топик для сообщений авторизации платежа */
    @Value("${app.kafka.topics.authorization}")
    private String authorizationTopic;

    /** Топик для сообщений отмены платежа */
    @Value("${app.kafka.topics.cancel}")
    private String cancelTopic;

    /** Топик для сообщений проведения платежа */
    @Value("${app.kafka.topics.clearing}")
    private String clearingTopic;

    /**
     * Отправка сообщения авторизации платежа.
     *
     * @param message объект с данными платежа
     */
    public void sendAuthorization(PaymentMessageDto message) {
        sendMessage(authorizationTopic, message);
    }

    /**
     * Отправка сообщения об отмене платежа.
     *
     * @param message объект с данными платежа
     */
    public void sendCancel(PaymentMessageDto message) {
        sendMessage(cancelTopic, message);
    }

    /**
     * Отправка сообщения о проведении платежа.
     *
     * @param message объект с данными платежа
     */
    public void sendClearing(PaymentMessageDto message) {
        sendMessage(clearingTopic, message);
    }

    /**
     * Вспомогательный метод для отправки сообщения в Kafka.
     * Логирует успешную отправку и использует {@code idempotencyToken} в качестве ключа сообщения.
     *
     * @param topic   топик для отправки сообщения
     * @param message объект с данными платежа
     */
    private void sendMessage(String topic, PaymentMessageDto message) {
        kafkaTemplate.send(topic, message.getIdempotencyToken().toString(), message);
        log.info("Message sent successfully to topic {}: {}", topic, message);
    }
}