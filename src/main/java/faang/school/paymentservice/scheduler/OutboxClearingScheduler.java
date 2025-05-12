package faang.school.paymentservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxClearingScheduler {

    @Scheduled(cron = "${scheduled-setting.outbox-clearing.cron}")
    public void clearOutbox() {

    }
}
