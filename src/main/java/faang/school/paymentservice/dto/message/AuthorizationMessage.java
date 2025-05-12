package faang.school.paymentservice.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class AuthorizationMessage implements PaymentOperationMessage {
    @NotNull
    private UUID operationId;

    @NotNull
    private UUID senderAccountId;

    @NotNull
    private UUID recipientAccountId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private String currency;

    @NotNull
    private Instant timestamp;
}
