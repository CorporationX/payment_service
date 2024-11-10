package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.service.payment.PaymentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PaymentRequestScheduler {

    private PaymentRequestService requestService;

    @Scheduled(cron = "${payment-request.cron}")
    public void pushPaymentConfirmation() {
        requestService.pushPaymentConfirmation();
    }
}
