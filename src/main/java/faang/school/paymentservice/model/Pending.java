package faang.school.paymentservice.model;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.enums.RequestStatus;
import faang.school.paymentservice.enums.RequestType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pending")
public class Pending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "senders_account_number", nullable = false, length = 32)
    private String accountNumber;

    @Column(name = "recipient_id")
    private Long recipientId;

    @Column(name = "balance", nullable = false, columnDefinition = "NUMERIC(15,2) DEFAULT 0.00")
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Column(name = "operation_id", nullable = false, length = 64)
    private String operationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus requestStatus;

    @Column(name = "reason", length = 256)
    private String reason;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @PrePersist
    private void setDefaultCompletionDate(){
        if (this.completionDate == null){
            this.completionDate = LocalDateTime.now().plusDays(1);
        }
    }
}
