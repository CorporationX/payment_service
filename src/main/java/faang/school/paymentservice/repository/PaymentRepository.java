package faang.school.paymentservice.repository;

import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Request, Long> {
    @Query(
            "SELECT r FROM Request r WHERE r.verificationCode = :verificationCode"
    )
    Optional<Request> findRequestByVerificationCode(@Param("verificationCode") String verificationCode);

    @Query(
            "SELECT s FROM Request s WHERE s.status = :status AND s.clearScheduledAt < :time"
    )
    List<Request> findByStatus(@Param("status") PaymentStatus status, @Param("time") LocalDateTime time);
}
