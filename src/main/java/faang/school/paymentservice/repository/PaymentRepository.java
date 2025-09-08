package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.enums.PaymentStages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdempotencyToken(UUID idempotencyToken);

    default Payment getByIdempotencyTokenOrThrow(UUID idempotencyToken) {
        return findByIdempotencyToken(idempotencyToken)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for idempotencyToken: " + idempotencyToken));
    }

    List<Payment> findByStatus(PaymentStages status);

    /**
     * Находит все платежи с статусом {@code PENDING},
     * для которых время запланированного клиринга {@code clearScheduledAt} уже наступило или прошло.
     * <p>
     * Используется для того, чтобы обрабатывать платежи, которые ожидают клиринга и пора их списывать.
     *
     * @return список платежей {@link Payment}, которые удовлетворяют условиям
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.clearScheduledAt <= CURRENT_TIMESTAMP")
    List<Payment> findPendingWithScheduledBefore();
}