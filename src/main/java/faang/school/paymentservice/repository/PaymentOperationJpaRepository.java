package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.PaymentOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentOperationJpaRepository extends JpaRepository<PaymentOperation, Long> {
}
