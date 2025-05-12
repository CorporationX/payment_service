package faang.school.paymentservice.repository;

import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.model.PaymentOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentOperationRepository extends JpaRepository<PaymentOperation, UUID> {

    @Query("""
            SELECT p FROM PaymentOperation p
            WHERE p.paymentStatus = :status AND p.clearScheduledAt < :time
            ORDER BY p.clearScheduledAt
            """)
    List<PaymentOperation> findOperationForForcedClearing(
            @Param("status") PaymentStatus status,
            @Param("time") Instant time,
            Pageable pageable
    );
}
