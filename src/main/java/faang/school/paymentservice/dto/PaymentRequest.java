package faang.school.paymentservice.dto;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull
        long paymentNumber,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency currency
) {
}
