package faang.school.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Evgenii Malkov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "status", length = 10, nullable = false)
    private String status;

    @Column(name = "payment_number")
    private Long paymentNumber;

    @Column(name = "request_id", length = 100, nullable = false)
    private String requestId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_datetime", nullable = false)
    private LocalDateTime createDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expired_datetime")
    private LocalDateTime expiredDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "paid_datetime")
    private LocalDateTime paidDateTime;

    @Column(name = "product", nullable = false)
    private String product;
}
