package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(value = "SELECT * FROM transaction WHERE scheduled_at <= NOW() AND status = 'READY_TO_CLEAR' ORDER BY scheduled_at LIMIT 10", nativeQuery = true)
    List<Payment> findReadyToClearTransactions();

    Optional<Payment> findPaymentByIdempotencyKey(UUID idempotencyKey);
}