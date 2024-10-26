package faang.school.paymentservice.dto.request;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.entity.request.RequestStatus;

import java.time.LocalDateTime;

public record RequestDto(
    Long id,
    Long senderId,
    Long receiverId,
    Double amount,
    Currency currency,
    RequestStatus status,
    LocalDateTime clearScheduledAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {
}
