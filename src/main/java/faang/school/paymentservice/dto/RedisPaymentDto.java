package faang.school.paymentservice.dto;

import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.enums.TransactionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RedisPaymentDto{
        long userId;
        long senderBalanceNumber;
        long getterBalanceNumber;
        @Min(1)
        BigDecimal amount;
        @NotNull
        Currency currency;
        @NotNull
        TransactionStatus status;
}
