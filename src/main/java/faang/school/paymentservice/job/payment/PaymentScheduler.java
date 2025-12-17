package faang.school.paymentservice.job.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentScheduler {

    private final PaymentSchedulerService paymentSchedulerService;

    @Scheduled(cron = "${app.scheduled.payments.clearing-scheduling}", zone = "Europe/Moscow")
    public void clearingSchedulingPayments() {
        paymentSchedulerService.clearingSchedulingPayments();
    }
}
