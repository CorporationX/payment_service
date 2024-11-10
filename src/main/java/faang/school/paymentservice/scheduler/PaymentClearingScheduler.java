package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.service.payment.PaymentOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentClearingScheduler {

    private final PaymentOperationService paymentOperationService;

    @Scheduled(cron = "${payment-clearing-scheduler.cron}")
    public void executeScheduledPaymentClearing() {
        log.info("executeScheduledPaymentClearing() - start");
        paymentOperationService.processClearingPayments();
        log.info("executeScheduledPaymentClearing() - finish");
    }
}
