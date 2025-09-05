package faang.school.paymentservice.service;

import faang.school.paymentservice.model.Payment;
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

    @Scheduled(fixedDelayString = "${payment.clearing.fixed-delay-ms}")
    @Transactional
    public void processScheduledClearings() {
        List<Payment> toClear = paymentRepository.findPendingWithScheduledBefore();

        for (Payment payment : toClear) {
            log.debug("Scheduled clearing for idempotencyToken={}", payment.getIdempotencyToken());

            try {
                paymentService.confirmPayment(payment.getIdempotencyToken());
            } catch (Exception ex) {
                log.error("Не удалось подтвердить оплату для idempotencyToken={}: {}",
                        payment.getIdempotencyToken(), ex.getMessage(), ex);
            }
        }
    }
}