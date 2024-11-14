package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.event.PaymentApproveEvent;
import faang.school.paymentservice.dto.event.PaymentCancelEvent;
import faang.school.paymentservice.dto.event.PaymentRequestEvent;

public interface PaymentService {
    void requestPayment(PaymentRequestEvent event);
    void cancelPayment(PaymentCancelEvent event);
    void approvePayment(PaymentApproveEvent event);
}
