package faang.school.paymentservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "amount", updatable = false, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 8, updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "account_number_from", length = 20, updatable = false, nullable = false)
    private String accountNumberFrom;

    @Column(name = "account_number_to", length = 20, updatable = false, nullable = false)
    private String accountNumberTo;

    @Column(name = "status", length = 16, nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "clear_scheduled_at", updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime clearScheduledAt;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
}
