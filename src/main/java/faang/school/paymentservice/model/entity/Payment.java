package faang.school.paymentservice.model.entity;

import faang.school.paymentservice.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pending_operation")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receiver_account_number", updatable = false, nullable = false)
    private String receiverAccountNumber;

    @Column(name = "owner_account_number", updatable = false, nullable = false)
    private String ownerAccountNumber;

    @Column(name = "idempotency_token", updatable = false, nullable = false)
    private String idempotencyToken;

    @Column(name = "currency", updatable = false, nullable = false)
    private String currency;

    @Column(name = "amount", updatable = false, nullable = false)
    private BigDecimal amount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "clear_scheduled_at", updatable = false, insertable = false)
    private LocalDateTime clearScheduledAt;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
}
