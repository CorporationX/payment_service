package faang.school.paymentservice.publisher;

import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Компонент для публикации платежных событий в Kafka.
 * <p>
 * Основные функции:
 * <ul>
 *   <li>Пакетная публикация событий с указанным статусом</li>
 *   <li>Интеграция с {@link SendEvent} для фактической отправки в Kafka</li>
 *   <li>Поддержка пакетной обработки через конфигурируемый размер batch</li>
 * </ul>
 *
 * <p>Конфигурация:
 * <ul>
 *   <li><code>publisher.batch</code> - размер пакета для обработки (по умолчанию: 100)</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class PaymentMessagePublisher {
    private final SendEvent sendEvent;
    private final OutboxEventRepository outboxEventRepository;

    @Value("${publisher.batch}")
    private int batch;

    public void publishEvent(OutboxStatus status) {
        Pageable pageable = PageRequest.of(0, batch);
        List<OutboxEvent> newEvents = outboxEventRepository.findByOutboxStatus(status, pageable);

        for (OutboxEvent event : newEvents) {
            sendEvent.sendEventToKafka(event);
        }
    }

}
