package faang.school.paymentservice.dto.event;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.entity.OwnerType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentOperationEvent(
        OperationType operationType,
        Long senderId,
        OwnerType senderType,
        Long receiverId,
        OwnerType receiverType,
        BigDecimal amount,
        Currency currency,
        LocalDateTime clearScheduledAt
) {}
