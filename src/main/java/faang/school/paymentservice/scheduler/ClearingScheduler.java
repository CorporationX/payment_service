package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.service.PendingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClearingScheduler {

    private final PendingService pendingService;

    @Value("${clearing.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${clearing.cron}")
    public void clearing() {
        log.info("Starting clearing scheduler");

        int pageNumber = 0;
        boolean isEmpty = false;

        while (!isEmpty) {
            PageRequest pageable = PageRequest.of(pageNumber, batchSize);
            isEmpty = pendingService.cleanBatch(pageable);
            pageNumber++;
        }

        log.info("Finished clearing scheduler");
    }
}
