package faang.school.paymentservice.dto;

import faang.school.paymentservice.enums.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreatePaymentRequest {

    private long senderBalanceNumber;
    private long getterBalanceNumber;

    @Min(1)
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    @NotNull
    private LocalDateTime clearScheduledAt;
}
