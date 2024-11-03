package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.OperationState;
import faang.school.paymentservice.model.PendingOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PendingRepository extends JpaRepository<PendingOperation, Long> {
    Optional<PendingOperation> findByOperationKey(String operationKey);
    List<PendingOperation> findByState(OperationState operationState);
}
