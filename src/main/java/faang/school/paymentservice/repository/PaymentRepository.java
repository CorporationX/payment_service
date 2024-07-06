package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(value = """
            select from payments 
            where scheduled_at is not null 
            and status = 'AUTHORIZATION'""", nativeQuery = true)
    List<Payment> findAllScheduledPayments();

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);
}