package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.Pending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PendingRepository extends JpaRepository<Pending, Long> {

    @Query("""
                    SELECT p FROM Pending p WHERE p.token = :token
            """)
    Optional<Pending> findByToken(UUID token);
}
