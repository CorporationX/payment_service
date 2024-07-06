package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRequestJpaRepository extends JpaRepository<PaymentRequest, Long> {
}
