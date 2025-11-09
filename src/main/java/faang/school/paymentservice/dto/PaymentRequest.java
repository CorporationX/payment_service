package faang.school.paymentservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "Payment number must not be null")
        Long paymentNumber,

        @NotNull(message = "Amount must not be null")
        @Min(value = 1, message = "Amount must be at least 1")
        BigDecimal amount,

        @NotNull(message = "Currency must not be null")
        Currency currency
) {
}
