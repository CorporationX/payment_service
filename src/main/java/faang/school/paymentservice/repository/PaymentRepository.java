package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.enums.PaymentStages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdempotencyToken(UUID idempotencyToken);

    default Payment getByIdempotencyTokenOrThrow(UUID idempotencyToken) {
        return findByIdempotencyToken(idempotencyToken)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for idempotencyToken: " + idempotencyToken));
    }

    List<Payment> findByStatus(PaymentStages status);

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.clearScheduledAt <= :now")
    List<Payment> findPendingWithScheduledBefore(LocalDateTime now);
}