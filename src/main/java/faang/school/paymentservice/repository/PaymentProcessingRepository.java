package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.PaymentOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentProcessingRepository extends JpaRepository<PaymentOperation, Long> {
}
