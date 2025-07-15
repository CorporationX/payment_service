package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.Pending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PendingRepository extends JpaRepository<Pending, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM pending WHERE operation_id = ?1 FOR UPDATE
            """)
    Optional<Pending> findByOperationId(String id);

    @Query(nativeQuery = true, value = """
            SELECT * FROM pending WHERE completion_date < CURRENT_TIMESTAMP AND status = 'PENDING'
            """)
    List<Pending> findAllByCompletionDate();
}
