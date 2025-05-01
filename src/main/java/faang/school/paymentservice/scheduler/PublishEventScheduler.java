package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.publisher.PaymentMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublishEventScheduler {
    private final PaymentMessagePublisher paymentMessagePublisher;

    @Scheduled(cron = "${scheduler.publisher.cron}")
    public void publishEventScheduler() {
        paymentMessagePublisher.publishEvent(OutboxStatus.NEW);
    }
}
