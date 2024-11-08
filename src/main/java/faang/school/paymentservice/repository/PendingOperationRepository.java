package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingOperationRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdempotencyToken(String idempotencyToken);
}
