package faang.school.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull
        long paymentNumber,

        @DecimalMin("1.00")
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency currency
) {
}
