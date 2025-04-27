package faang.school.paymentservice.repository;

import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.model.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Query("SELECT e FROM OutboxEvent e WHERE e.outboxStatus = 'NEW' ORDER BY e.createdAt")
    List<OutboxEvent> findByOutboxStatus(OutboxStatus outboxStatus, Pageable pageable);
}
