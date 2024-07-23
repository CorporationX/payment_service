package faang.school.paymentservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentDto(
        @NotNull
        String requestId,
        @Min(1)
        @NotNull
        BigDecimal amount,
        @NotNull
        Product product,
        @NotNull
        Currency currency
) {
}
