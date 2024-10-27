package faang.school.paymentservice.dto.event;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequestEvent {
    private long userId;

    @Positive
    private BigDecimal amount;

    private String operationKey;
}
