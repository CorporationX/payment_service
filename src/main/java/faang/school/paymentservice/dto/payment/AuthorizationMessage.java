package faang.school.paymentservice.dto.payment;

import faang.school.paymentservice.dto.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class AuthorizationMessage {
    @NotNull
    private Long senderAccountId;
    @NotNull
    private Long recipientAccountId;
    @NotNull
    private String senderNumber;
    @NotNull
    private String recipientAccountNumber;
    @NotNull
    private Currency currency;
    @NotNull
    @Min(1)
    private BigDecimal amount;
    private LocalDateTime clearScheduledAt;
}
