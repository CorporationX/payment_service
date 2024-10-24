package faang.school.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "Payment number cannot be null")
        long paymentNumber,

        @Min(value = 1, message = "Amount must be greater than or equal to 1")
        @NotNull(message = "Amount cannot be null")
        BigDecimal amount,

        @NotNull(message = "From currency cannot be null")
        Currency fromCurrency,

        @NotNull(message = "To currency cannot be null")
        Currency toCurrency
) {
}
