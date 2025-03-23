package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findAllByPaymentStatus(PaymentStatus paymentStatus);

    List<Payment> findAllByPaymentStatusAndPaymentDateTimeBefore(PaymentStatus paymentStatus,
                                                                 LocalDateTime paymentDateTime);

    @Modifying
    @Query(value = "UPDATE Payment SET payment_status = 'ERROR'  WHERE id = :id", nativeQuery = true)
    void setErrorPaymentStatus(@Param("id") UUID paymentId);
}
