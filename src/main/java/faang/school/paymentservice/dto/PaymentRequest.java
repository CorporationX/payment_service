package faang.school.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

import java.util.UUID;

@Schema(description = "Payment request data")
public record PaymentRequest(
        @Schema(
                description = "Unique identifier of the sender account",
                example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        UUID senderAccountId,

        @Schema(
                description = "Unique identifier of the recipient account",
                example = "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        UUID recipientAccountId,

        @Schema(
                description = "Payment amount (must be positive)",
                example = "100.50",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Min(1)
        @NotNull
        BigDecimal amount,

        @Schema(
                description = "Currency code",
                example = "USD",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Currency currency,

        @Schema(
                description = "Scheduled clearing timestamp in UTC",
                example = "2023-12-31T23:59:59Z",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Instant clearScheduledAt
)
{

}
