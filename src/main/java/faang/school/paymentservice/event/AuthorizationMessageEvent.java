package faang.school.paymentservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AuthorizationMessageEvent {
    private String accountNumber;
    private BigDecimal amount;
    private String idempotencyToken;
}
