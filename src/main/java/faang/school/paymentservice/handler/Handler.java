package faang.school.paymentservice.handler;

import faang.school.paymentservice.dto.event.PaymentApproveEvent;
import faang.school.paymentservice.dto.event.PaymentCancelEvent;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Handler {
    private final PaymentService paymentService;

    public void handle(PaymentApproveEvent event) {
        paymentService.approvePayment(event);
    }

    public void handle(PaymentCancelEvent event) {
        paymentService.cancelPayment(event);
    }
}
