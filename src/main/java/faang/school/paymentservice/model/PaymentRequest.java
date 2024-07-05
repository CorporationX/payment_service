package faang.school.paymentservice.model;

import faang.school.paymentservice.dto.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_request")
public class PaymentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "debit_account_id", nullable = false)
    private Long debitAccountId;

    @Column(name = "credit_account_id", nullable = false)
    private Long creditAccountId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OperationType type;

    @Column(name = "is_cleared", nullable = false)
    private Boolean isCleared;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "clear_scheduled_at", nullable = false)
    private LocalDateTime clearScheduledAt;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean shouldClearNow() {
        return clearScheduledAt.isBefore(LocalDateTime.now());
    }
}