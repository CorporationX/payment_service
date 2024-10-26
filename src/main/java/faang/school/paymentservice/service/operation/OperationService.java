package faang.school.paymentservice.service.operation;

import faang.school.paymentservice.dto.event.PaymentRequestEvent;

public interface OperationService {
    void savePendingOperation(PaymentRequestEvent event);
    void updatePendingOperation(PaymentRequestEvent event);
}
