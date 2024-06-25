package faang.school.paymentservice.scheduler.payment.request;

import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentRequestApproveScheduler {

    private final PaymentService paymentService;

    @Value("${scheduler.force-request.limit}")
    private Long limit;

    @Scheduled(fixedRateString = "${scheduler.force-request.period}")
    @Transactional
    public void approveRequestsForPayment() {
        paymentService.approvePendingRequests(limit);
    }
}
