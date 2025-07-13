package faang.school.paymentservice.event.payment;

import java.time.LocalDateTime;
import java.util.UUID;

public record SuccessPaymentAuthorizationEventDto (UUID operationToken,
                                                   UUID operationId,
                                                   LocalDateTime timestamp,
                                                   String detail) {
}
