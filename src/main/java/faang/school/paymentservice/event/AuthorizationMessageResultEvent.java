package faang.school.paymentservice.event;

import faang.school.paymentservice.dto.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthorizationMessageResultEvent {
    private String accountNumber;
    private PaymentStatus paymentStatus;
    private String idempotencyToken;
}
