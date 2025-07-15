package faang.school.paymentservice.entity;

import faang.school.paymentservice.dto.TransferStage;
import faang.school.paymentservice.dto.TransactionType;
import faang.school.paymentservice.dto.TransferStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transfer")
public class Transfer {

    @Id
    @GeneratedValue
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID id;

    @Column(name = "initiator_id", updatable = false)
    private Long initiatorId;

    @Column(name = "account_event_id", unique = true)
    private UUID accountEventId;

    @Column(name = "source_account_id", nullable = false, unique = true, updatable = false)
    private UUID sourceAccountId;

    @Column(name = "target_account_id", nullable = false, unique = true, updatable = false)
    private UUID targetAccountId;

    @Column(name = "transfer_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStatus transferStatus;

    @Column(name = "transfer_stage", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStage transferStage;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "to_be_cleared_after", nullable = false)
    private LocalDateTime clearedAfter;

    @Version
    private Integer version;

}
