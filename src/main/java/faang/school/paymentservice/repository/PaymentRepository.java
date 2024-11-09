package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM payments
            WHERE status = 'AUTH_SUCCESS'
            AND clear_scheduled_at < NOW()
            """)
    List<Payment> getReadyForClearingPayments();

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}
