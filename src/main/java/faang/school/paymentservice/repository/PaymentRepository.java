package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Request, Long> {
}
