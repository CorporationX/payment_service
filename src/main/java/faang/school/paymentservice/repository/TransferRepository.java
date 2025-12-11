package faang.school.paymentservice.repository;

import faang.school.paymentservice.exception.EntityNotFoundException;
import faang.school.paymentservice.model.Transfer;
import faang.school.paymentservice.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID> {

    @Query(value = """
            select tr.id
            from Transfer tr
            where tr.status = :paymentStatus
            and tr.clearScheduledAt <= :localDateTime
            """)
    List<UUID> findIdsByStatusAndClearScheduledAt(PaymentStatus paymentStatus, LocalDateTime localDateTime);

    @Query("""
            select tr.id
            from Transfer tr
            where tr.status = :paymentStatus
            """)
    List<UUID> findIdsByStatus(PaymentStatus paymentStatus);

    default Transfer findByIdOrThrow(UUID id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transfer not found %s".formatted(id)));
    }
}
