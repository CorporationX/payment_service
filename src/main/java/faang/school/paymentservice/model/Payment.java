package faang.school.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private UUID id;

        @Column(name = "idempotency_key", length = 64, nullable = false)
        private String idempotencyKey;

        @Column(name = "source_account_id", nullable = false)
        private UUID sourceAccountId;

        @Column(name = "target_account_id", nullable = false)
        private UUID targetAccountId;

        @Column(nullable = false)
        private BigDecimal amount;

        @Column(nullable = false, length = 3)
        @Enumerated(EnumType.STRING)
        private Currency currency;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private PaymentStatus status;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private PaymentCategory category;

        @Column(name = "clear_scheduled_at", nullable = false)
        private LocalDateTime clearScheduledAt;

        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;
}
