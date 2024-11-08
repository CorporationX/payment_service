package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.service.PendingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClearingScheduler {

    private final PendingService pendingService;

    @Scheduled(cron = "${clearing.cron}")
    public void clearing() {
        log.info("Starting clearing scheduler");
        pendingService.cleaningPendings();
        log.info("Finished clearing scheduler");
    }
}
