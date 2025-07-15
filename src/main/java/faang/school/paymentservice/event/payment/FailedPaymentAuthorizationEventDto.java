package faang.school.paymentservice.event.payment;

import java.time.LocalDateTime;
import java.util.UUID;


public record FailedPaymentAuthorizationEventDto (UUID operationToken,
                                                  LocalDateTime timestamp,
                                                  String detail) {
}

