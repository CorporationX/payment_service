package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.publisher.PaymentMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Планировщик для публикации событий о платежах.
 * <p>
 * Регулярно (по расписанию) запускает процесс публикации новых событий
 * через {@link PaymentMessagePublisher}.
 *
 * <p>Расписание задается в конфигурации свойствами:
 * <ul>
 *   <li><code>scheduler.publisher.cron</code> - cron-выражение для расписания</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class PublishEventScheduler {
    private final PaymentMessagePublisher paymentMessagePublisher;

    @Scheduled(cron = "${scheduler.publisher.cron}")
    public void publishEventScheduler() {
        paymentMessagePublisher.publishEvent(OutboxStatus.NEW);
    }
}
