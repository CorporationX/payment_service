package faang.school.paymentservice.entity;

import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.enums.PaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "sender_account_number", updatable = false, nullable = false)
    private String senderAccountNumber;

    @Column(name = "receiver_account_number", updatable = false, nullable = false)
    private String receiverAccountNumber;

    @Column(name = "amount", updatable = false, nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", updatable = false, nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Builder.Default
    @Column(name = "creat_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "payment_date_time", nullable = false)
    private LocalDateTime paymentDateTime;

    @Column(name = "payment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Builder.Default
    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.NEW;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @PrePersist
    private void initializeDefaultValue() {
        if (paymentType == null) {
            paymentType = PaymentType.OTHER;
        }

        if (paymentDateTime == null) {
            paymentDateTime = LocalDateTime.now();
        }

    }
}

