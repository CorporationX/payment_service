package faang.school.paymentservice.service;

import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Планировщик для проведения запланированных платежей (clearing).
 * <p>
 * Периодически проверяет платежи со статусом {@code PENDING}, у которых установлено
 * время проведения (clearScheduledAt) до текущего момента, и подтверждает их через {@link PaymentService}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentClearingScheduler {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    /**
     * Обработка запланированных платежей.
     * <p>
     * Вызывается с периодом, указанным в {@code payment.clearing.fixed-delay-ms}.
     * Для каждого найденного платежа вызывается подтверждение через {@link PaymentService}.
     * В случае ошибки логируется исключение.
     */
    @Scheduled(fixedDelayString = "${payment.clearing.fixed-delay-ms}")
    @Transactional
    public void processScheduledClearings() {
        List<Payment> toClear = paymentRepository.findPendingWithScheduledBefore();

        for (Payment payment : toClear) {
            log.debug("Запланированное проведение платежа для idempotencyToken={}", payment.getIdempotencyToken());

            try {
                paymentService.confirmPayment(payment.getIdempotencyToken());
            } catch (Exception ex) {
                log.error("Не удалось подтвердить платеж для idempotencyToken={}: {}",
                        payment.getIdempotencyToken(), ex.getMessage(), ex);
            }
        }
    }
}