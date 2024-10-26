package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.event.PaymentApproveEvent;
import faang.school.paymentservice.dto.event.PaymentCancelEvent;
import faang.school.paymentservice.dto.event.PaymentRequestEvent;
import faang.school.paymentservice.publisher.PaymentRequestEventPublisher;
import faang.school.paymentservice.service.operation.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRequestEventPublisher eventPublisher;
    private final OperationService operationService;

    public void requestPayment(PaymentRequestEvent event) {
        operationService.savePendingOperation(event);
        eventPublisher.publish(event);
    }

    public void cancelPayment(PaymentCancelEvent event) {

    }

    public void approvePayment(PaymentApproveEvent event) {

    }
}
