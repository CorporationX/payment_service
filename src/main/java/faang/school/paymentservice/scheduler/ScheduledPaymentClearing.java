package faang.school.paymentservice.scheduler;


import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.paymentservice.model.PaymentStatus.SCHEDULED_PENDING;

@Component
@RequiredArgsConstructor
public class ScheduledPaymentClearing {
    private final PaymentService paymentService;

    @Scheduled(cron = "${payment.clearing.scheduler.cron}")
    public void doScheduledClearing() {
        List<Payment> readyForClearing = paymentService.getPaymentsForClearing();
        readyForClearing.forEach(payment -> paymentService.updatePaymentStatus(payment.getId(), SCHEDULED_PENDING));
    }
}
