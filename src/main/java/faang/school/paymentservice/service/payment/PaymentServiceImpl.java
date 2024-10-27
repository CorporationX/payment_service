package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.event.PaymentApproveEvent;
import faang.school.paymentservice.dto.event.PaymentCancelEvent;
import faang.school.paymentservice.dto.event.PaymentRequestEvent;
import faang.school.paymentservice.publisher.PaymentRequestEventPublisher;
import faang.school.paymentservice.service.operation.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRequestEventPublisher eventPublisher;
    private final OperationService operationService;

    public void requestPayment(PaymentRequestEvent event) {
        event.setOperationKey(makeOperationKey(event));
        operationService.savePendingOperation(event);
        eventPublisher.publish(event);
    }

    private String makeOperationKey(PaymentRequestEvent event) {
        return LocalDateTime.now().toString() + "_" + event.getUserId() + "_" + event.getAmount();
    }

    public void cancelPayment(PaymentCancelEvent event) {

    }

    public void approvePayment(PaymentApproveEvent event) {

    }
}
