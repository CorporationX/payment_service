package faang.school.paymentservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponse {
    private Long paymentNumber;
    private PaymentStatus status;
    private Long debitAccountId;
    private Long creditAccountId;
    private BigDecimal amount;
    private Currency currency;
    private String message;
}
