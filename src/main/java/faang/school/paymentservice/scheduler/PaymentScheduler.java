package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduler {
    private final PaymentService paymentService;
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Async
    @Scheduled(cron = "${scheduler.payment_scheduler.cron}")
    public void schedulePayments() {
        List<Payment> payments = paymentService.getScheduledPayments();
        log.info("payments for scheduling: {}", payments);
        for (Payment payment : payments) {
            threadPoolTaskScheduler.schedule(() ->
                    paymentService.clear(payment.getId()),
                    payment.getScheduledAt().toInstant(ZoneOffset.UTC));
            log.info("payment cleared: {}", payment);
        }
    }
}