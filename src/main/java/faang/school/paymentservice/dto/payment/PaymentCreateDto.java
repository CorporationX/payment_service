package faang.school.paymentservice.dto.payment;

import faang.school.paymentservice.utils.idempotencyKey.IdempotencyKeyGenerator;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDto {

    @NotNull(message = "Source account ID is required")
    private UUID sourceAccountId;

    @NotNull(message = "Target account ID is required")
    private UUID targetAccountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Clear scheduled time is required")
    @Future(message = "Clear scheduled time must be in the future")
    private LocalDateTime clearScheduledAt;

    public String getIdempotencyKey() {
        String rawKey = getSourceAccountId().toString() +
                getTargetAccountId().toString() +
                getAmount().toString() +
                getCurrency() +
                getCategory() +
                getClearScheduledAt().toString();

        return IdempotencyKeyGenerator.hash(rawKey);
    }
}