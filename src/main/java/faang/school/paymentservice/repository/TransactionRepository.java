package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT * FROM transaction WHERE scheduled_at <= NOW() AND status = 'READY_TO_CLEAR' ORDER BY scheduled_at LIMIT 10", nativeQuery = true)
    List<Transaction> findReadyToClearTransactions();
}