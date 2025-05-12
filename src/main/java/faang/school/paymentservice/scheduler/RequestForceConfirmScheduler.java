package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestForceConfirmScheduler {

    private final PaymentProcessingService paymentProcessingService;

    @Scheduled(cron = "${scheduled-setting.request-force-confirming.cron}")
    public void transferPaymentsToStatusConfirmed() {
        log.info("Transfer payments to status CONFIRMED start at {}", LocalDateTime.now());
        paymentProcessingService.findPaymentsReadyToConfirmed();
        log.info("Transfer payments to status CONFIRMED end at {}", LocalDateTime.now());
    }
}
