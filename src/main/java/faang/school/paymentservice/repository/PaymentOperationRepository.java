package faang.school.paymentservice.repository;

import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.model.PaymentOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentOperationRepository extends JpaRepository<PaymentOperation, UUID> {

    List<PaymentOperation> findByPaymentStatusAndClearScheduledAtBefore(PaymentStatus status, Instant time);
}
