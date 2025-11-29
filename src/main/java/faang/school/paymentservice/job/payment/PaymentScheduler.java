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

    @Scheduled(cron = "${app.scheduled.payments.retry-authorization-error}", zone = "Europe/Moscow")
    public void retryAuthorizationErrorPayments() {
        paymentSchedulerService.retryAuthorizationErrorPayments();
    }

    @Scheduled(cron = "${app.scheduled.payments.retry-clearing-error}", zone = "Europe/Moscow")
    public void retryClearingErrorPayments() {
        paymentSchedulerService.retryClearingErrorPayments();
    }

    @Scheduled(cron = "${app.scheduled.payments.retry-cancel-error}", zone = "Europe/Moscow")
    public void retryCancelErrorPayments() {
        paymentSchedulerService.retryCancelErrorPayments();
    }
}
