package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.CreatePaymentRequest;
import faang.school.paymentservice.model.BalanceAudit;
import org.springframework.stereotype.Component;

@Component
public interface PaymentService {
    BalanceAudit createRequestForPayment(CreatePaymentRequest dto, long userId);

    BalanceAudit cancelRequestForPayment(Long balanceAuditId, long userId);

    BalanceAudit forceRequestForPayment(Long balanceAuditId, long userId);
    void approvePendingRequests(Long limit);
}
