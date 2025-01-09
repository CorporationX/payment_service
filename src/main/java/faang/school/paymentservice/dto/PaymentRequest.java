package faang.school.paymentservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
public record PaymentRequest(
        @NotNull
        String receiverAccountNumber,

        @NotNull
        String ownerAccountNumber,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency currency,

        @NotNull
        @NotBlank
        String idempotencyToken
) {
}
