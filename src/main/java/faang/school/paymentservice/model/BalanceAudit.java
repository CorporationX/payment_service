package faang.school.paymentservice.model;

import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.enums.PaymentStatus;
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
@Table(name = "balance_audit")
public class BalanceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @JoinColumn(name = "sender_balance_id", referencedColumnName = "id")
    private Balance senderBalance;

    @OneToOne
    @JoinColumn(name = "getter_balance_id", referencedColumnName = "id")
    private Balance getterBalance;

    @Column(name = "lock_value")
    private String lockValue;

    @Column(name = "authorization_amount")
    private BigDecimal authorizationAmount;

    @Column(name = "actual_amount")
    private BigDecimal actualAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus paymentStatus;

    @Column(name = "audit_timestamp")
    private LocalDateTime auditTimestamp;

    @Column(name = "clear_scheduled_at")
    private LocalDateTime clearScheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;
}

