package faang.school.paymentservice.repository.payment;

import faang.school.paymentservice.entity.payment.PaymentOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentOperationRepository extends JpaRepository<PaymentOperation, UUID> {
    Optional<PaymentOperation> findByOperationToken(UUID operationToken);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM payment_operation
            WHERE operation_token = :operationToken
            FOR UPDATE
            """)
    Optional<PaymentOperation> findByOperationTokenForUpdate(@Param("operationToken") UUID operationToken);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM payment_operation
            WHERE id = :paymentOperationId
            FOR UPDATE
            """)
    Optional<PaymentOperation> findByIdForUpdate(@Param("paymentOperationId") UUID paymentOperationId);

    @Query(nativeQuery = true, value = """
                    SELECT * FROM payment_operation
                    WHERE status = 'AUTHORIZED' AND clear_scheduled_at >= now()
                    ORDER BY clear_scheduled_at DESC
                    LIMIT 1
                    FOR UPDATE SKIP LOCKED
            """)
    Optional<PaymentOperation> findOperationReadyToClear();
}
