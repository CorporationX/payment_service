package faang.school.paymentservice.model.entity;

import faang.school.paymentservice.model.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pending_operation")
public class PendingOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "sender_account_id", updatable = false, nullable = false)
    private Long senderAccountId;

    @Column(name = "receiver_account_id", updatable = false, nullable = false)
    private Long receiverAccountId;

    @Column(name = "sender_currency", updatable = false, nullable = false)
    private Currency senderCurrency;

    @Column(name = "receiver_currency", updatable = false, nullable = false)
    private Currency receiverCurrency;

    @Column(name = "amount_sender_currency", updatable = false, nullable = false)
    private BigDecimal amountInSenderCurrency;

    @Column(name = "amount_receiver_currency", updatable = false, nullable = false)
    private BigDecimal amountInReceiverCurrency;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "status_details")
    private String statusDetails;

    @Column(name = "clear_scheduled_at", updatable = false, insertable = false)
    private LocalDateTime clearScheduledAt;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
}
