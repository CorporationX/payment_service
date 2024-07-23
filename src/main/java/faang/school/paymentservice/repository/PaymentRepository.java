package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Evgenii Malkov
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByRequestId(String requestId);
}
