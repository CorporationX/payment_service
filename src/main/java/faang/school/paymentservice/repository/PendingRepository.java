package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.PendingOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingRepository extends JpaRepository<PendingOperation, Long> {
}
