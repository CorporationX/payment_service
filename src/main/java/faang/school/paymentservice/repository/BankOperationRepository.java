package faang.school.paymentservice.repository;

import faang.school.paymentservice.exception.EntityNotFoundException;
import faang.school.paymentservice.model.BankOperation;
import faang.school.paymentservice.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BankOperationRepository extends JpaRepository<BankOperation, UUID> {

    @Query(value = """
            select ba.id
            from BankOperation ba
            where ba.status = :paymentStatus
            and ba.clearScheduledAt <= :localDateTime
            """)
    List<UUID> findIdsByStatusAndClearScheduledAt(PaymentStatus paymentStatus, LocalDateTime localDateTime);

    @Query("""
            select bo.id
            from BankOperation bo
            where bo.status = :paymentStatus
            """)
    List<UUID> findIdsByStatus(PaymentStatus paymentStatus);

    default BankOperation findByIdOrThrow(UUID id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BankOperation not found %s".formatted(id)));
    }
}
