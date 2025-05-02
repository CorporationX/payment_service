package faang.school.paymentservice.repository;

import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.model.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query("SELECT e FROM OutboxEvent e WHERE e.outboxStatus = :outboxStatus ORDER BY e.createdAt")
    List<OutboxEvent> findByOutboxStatus(OutboxStatus outboxStatus, Pageable pageable);

    @Query("SELECT NOT EXISTS (SELECT 1 FROM OutboxEvent e " +
            "WHERE e.paymentOperationId = :paymentOperationId " +
            "AND e.eventType = :eventType " +
            "AND e.outboxStatus = :outboxStatus " +
            "AND e.sentAt IS NOT NULL)")
    boolean notExistsSentAuth(@Param("paymentOperationId") UUID paymentOperationId,
                              @Param("eventType") PaymentStatus eventType,
                              @Param("outboxStatus") OutboxStatus outboxStatus);
}
