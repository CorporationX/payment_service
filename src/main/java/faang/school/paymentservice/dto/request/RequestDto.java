package faang.school.paymentservice.dto.request;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.entity.request.RequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RequestDto(
    Long id,

    @Positive
    long senderId,

    @Positive
    long receiverId,

    @Positive
    BigDecimal amount,

    @NotNull
    Currency currency,

    @NotNull
    LocalDateTime clearScheduledAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {
}
