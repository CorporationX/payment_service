package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.entity.PendingOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PendingOperationRepository1 extends JpaRepository<PendingOperation, UUID> {
}
