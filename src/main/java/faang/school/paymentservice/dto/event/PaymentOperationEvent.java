package faang.school.paymentservice.dto.event;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.entity.OwnerType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentOperationEvent(
        UUID idempotencyToken,
        OperationType operationType,
        Long senderId,
        Long receiverId,
        OwnerType receiverType,
        BigDecimal amount,
        Currency currency,
        LocalDateTime clearScheduledAt
) {}
