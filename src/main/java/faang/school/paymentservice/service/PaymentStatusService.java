package faang.school.paymentservice.service;

import faang.school.paymentservice.model.event.PaymentStatusEvent;

public interface PaymentStatusService {
    void processInProgressStatus(PaymentStatusEvent event);
    void processCompletedStatus(PaymentStatusEvent event);
    void processCancelledStatus(PaymentStatusEvent event);
    void processFailedStatus(PaymentStatusEvent event);

}
