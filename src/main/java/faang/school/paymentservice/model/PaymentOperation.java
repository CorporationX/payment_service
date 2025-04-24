package faang.school.paymentservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table( name = "payment_operaations")
public class PaymentOperation {
    @Id
    private UUID id;

    @Column(name = "sender_account_id", nullable = false)
    private UUID senderAccountId;

    @Column(name = "recipient_account_id", nullable = false)
    private UUID recipientAccountId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency_code",nullable = false)
    private String currencyCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "authorization_id", nullable = true)
    private String authorizationId;

    @Column(name = "clear_scheduled_at")
    private Instant clearScheduledAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    //optimistic locking
    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    public enum PaymentStatus{
        PENDING,
        AUTHORIZED,
        CLEARED, CANCELLED,
        FAILED
    }
}
