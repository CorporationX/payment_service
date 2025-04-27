package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.publisher.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublishEventScheduler {
    private final OutboxEventPublisher outboxEventPublisher;

    @Scheduled(cron = "${scheduler.publisher.cron}")
    public void publishEventScheduler() {
        outboxEventPublisher.publishEvent(OutboxStatus.NEW);
    }
}
