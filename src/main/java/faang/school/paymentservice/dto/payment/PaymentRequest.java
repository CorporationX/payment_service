package faang.school.paymentservice.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentRequest {
        @NotNull
        @Positive
        private long orderId;
        @Min(1)
        @NotNull
        private BigDecimal amount;
        @NotNull
        private Currency currency;
}
