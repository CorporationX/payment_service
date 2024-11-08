package faang.school.paymentservice.entity;

import faang.school.paymentservice.dto.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pending")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false)
    private UUID token;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private PendingStatus status;

    @Column(name = "amount",nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Column(name = "from_account_id", nullable = false)
    private Long fromAccountId;

    @Column(name = "to_account_id", nullable = false)
    private Long toAccountId;
}

