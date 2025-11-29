package faang.school.paymentservice.dto;

import faang.school.paymentservice.model.ProductCategory;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuthorizationDto(
        @NotNull
        UUID senderAccountId,

        @NotNull
        UUID recipientAccountId,

        @NotNull
        BigDecimal amount,

        @NotNull
        LocalDateTime clearScheduledAt,

        @NotNull
        ProductCategory productCategory
) {
}
