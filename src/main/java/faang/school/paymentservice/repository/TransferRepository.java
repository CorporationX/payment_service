package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {

    @Query(nativeQuery = true, value = """
            SELECT COUNT(t.id) FROM transfer t
            WHERE t.transfer_status = 'ACTIVE' and initiator_id = :userId
            """)
    Long countUserActiveTransactions(Long userId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM transfer t
            WHERE account_event_id = :accountEventId
            """)
    Transfer findByAccountEventId(UUID accountEventId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM transfer t
            WHERE t.transfer_status = 'ACTIVE' and transfer_stage = 'AUTHORIZED' and to_be_cleared_after <= NOW()
            FOR UPDATE
            SKIP LOCKED
            LIMIT :limit
            """)
    List<Transfer> getTransfersForAutoClearing(int limit);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(*) FROM transfer t
            WHERE t.transfer_status = 'ACTIVE' and transfer_stage = 'AUTHORIZED' and to_be_cleared_after <= NOW()
            """)
    int countTransfersForAutoClearing();

}
