package faang.school.paymentservice.service;

import faang.school.paymentservice.model.PendingOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ClearingJob {
    private final PendingOperationService pendingOperationService;

    @Scheduled(fixedDelayString = "${app.clearing.job.interval}")
    public void processPendingOperations() {
        LocalDateTime now = LocalDateTime.now();
        List<PendingOperation> operationsForClearing = pendingOperationService.getOperationsForClearing(now);

        operationsForClearing.forEach(operation -> {
            try {
                pendingOperationService.confirmOperation(operation.getId());
                log.info("Operation confirmed by job with ID: {}", operation.getId());
            } catch (Exception e) {
                log.error("Failed to confirm operation with ID {}: {}", operation.getId(), e.getMessage(), e);
            }
        });
    }
}
