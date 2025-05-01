package faang.school.paymentservice.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.outbox.OutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.UUID;


@Setter
@Getter
@Entity
@Table(name = "outbox_event")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "payment_operation_id", nullable = false)
    private UUID paymentOperationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private PaymentStatus eventType;

    @Type(JsonType.class)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "outbox_status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'NEW'")
    private OutboxStatus outboxStatus = OutboxStatus.NEW;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT NOW()")
    private OffsetDateTime createdAt;

    @Column(name = "sent_at", nullable = true, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime sentAt;
}
