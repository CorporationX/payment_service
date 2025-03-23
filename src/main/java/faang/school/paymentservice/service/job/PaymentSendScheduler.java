package faang.school.paymentservice.service.job;

import faang.school.paymentservice.service.kafka.producer.PaymentRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentSendScheduler {

    private final PaymentRequestService paymentRequestService;

    @Scheduled(cron = "${payment.cron.check-new}")
    void sendAuthorizeNewPayments() {
        log.info("Send authorization by new payments");
        try {
            paymentRequestService.sendAuthorizePayments();
        } catch (TaskRejectedException e) {
            log.error("Task for send authorization is already running");
        }
    }

    @Scheduled(cron = "${payment.cron.check-cancel}")
    void sendCancelPayments() {
        log.info("Send cancel payments");
        try {
            paymentRequestService.sendCancelPayments();
        } catch (TaskRejectedException e) {
            log.error("Task for send cancel is already running");
        }
    }

    @Scheduled(cron = "${payment.cron.check-clearing}")
    void sendClearingPayments() {
        log.info("Send clearing payments");
        try {
            paymentRequestService.sendClearingPayments();
        } catch (TaskRejectedException e) {
            log.error("Task for send clearing is already running");
        }
    }

}
