package faang.school.paymentservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

import java.util.UUID;

public record PaymentRequest(
        @NotNull
        UUID senderAccountId,

        @NotNull
        UUID recipientAccountId,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency currency,

        @NotNull
        Instant clearScheduledAt
)
{

}
