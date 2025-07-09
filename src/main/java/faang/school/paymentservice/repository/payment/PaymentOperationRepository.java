package faang.school.paymentservice.repository.payment;

import faang.school.paymentservice.entity.payment.PaymentOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentOperationRepository extends JpaRepository<PaymentOperation, UUID> {
}
