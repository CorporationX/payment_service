package faang.school.paymentservice.model;


import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requester_number", nullable = false)
    private BigInteger requesterNumber;

    @Column(name = "receiver_number", nullable = false)
    private BigInteger receiverNumber;

    @Column(name = "currency", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Currency currency;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "idempotency_key", unique = true)
    private UUID idempotencyKey;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;
}