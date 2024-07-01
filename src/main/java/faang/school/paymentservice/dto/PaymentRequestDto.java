package faang.school.paymentservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentRequestDto {
    @NotNull
    private Long debitAccountId;

    @NotNull
    private Long creditAccountId;

    @Min(value = 1, message = "The minimal amount for a payment operation is 1 unit of currency.")
    @NotNull
    private BigDecimal amount;

    @NotNull
    private Currency currency;
}