package faang.school.paymentservice.service;

import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.enums.PaymentStages;
import faang.school.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentClearingScheduler {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processScheduledClearings() {
        List<Payment> toClear = paymentRepository.findPendingWithScheduledBefore(LocalDateTime.now());

        for (Payment payment : toClear) {
            if (payment.getStatus() != PaymentStages.PENDING) continue;

            log.info("Scheduled clearing for idempotencyToken={}", payment.getIdempotencyToken());

            try {
                paymentService.confirmPayment(payment.getIdempotencyToken());
            } catch (Exception ex) {
                log.error("Failed to confirm payment for idempotencyToken={}: {}", payment.getIdempotencyToken(), ex.getMessage(), ex);
            }
        }
    }
}