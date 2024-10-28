package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.model.PendingOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PendingOperationRepository extends JpaRepository<PendingOperation, UUID> {
    Optional<PendingOperation> findByIdAndStatus(UUID id, OperationStatus status);

    List<PendingOperation> findByStatusAndClearScheduledAtBefore(OperationStatus status, LocalDateTime dateTime);

    Optional<PendingOperation> findByIdempotencyKey(String idempotencyKey);
}