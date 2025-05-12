package faang.school.paymentservice.dto.event;

import faang.school.paymentservice.entity.OperationType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ResponseOutboxEvent(
        UUID idempotencyToken,
        OperationType operationType
) {}
