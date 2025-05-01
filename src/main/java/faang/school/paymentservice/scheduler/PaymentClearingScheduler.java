package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.model.PaymentOperation;
import faang.school.paymentservice.repository.PaymentOperationRepository;
import faang.school.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentClearingScheduler {
    private final PaymentOperationRepository repository;
    private final PaymentService paymentService;

    @Scheduled(cron = "${scheduler.clearing.cron}")
    public void processScheduledClearing() {
        Instant now = Instant.now();
        List<PaymentOperation> operations =
                repository.findByPaymentStatusAndClearScheduledAtBefore(PaymentStatus.PENDING, now);

        operations.forEach(operation -> paymentService.forcedPayment(operation.getId()));
    }
}
