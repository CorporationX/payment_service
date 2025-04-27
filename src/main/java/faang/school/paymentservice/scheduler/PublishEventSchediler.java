package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.publisher.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublishEventSchediler {
    private final OutboxEventPublisher outboxEventPublisher;

    @Scheduled(cron = "${scheduler.publisher.cron}")
    public void publishEventScheduler() {
        outboxEventPublisher.publishEvent();
    }
}
