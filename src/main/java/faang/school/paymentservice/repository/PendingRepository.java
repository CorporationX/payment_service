package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.Pending;
import faang.school.paymentservice.entity.PendingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingRepository extends JpaRepository<Pending, Long> {

    @Query("""
            SELECT p FROM Pending p
            WHERE p.status = :status
            """)
    Page<Pending> findByStatus(PendingStatus status, Pageable pageable);
}
