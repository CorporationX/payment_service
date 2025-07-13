package faang.school.paymentservice.event.payment;

import java.time.LocalDateTime;
import java.util.UUID;

public record FailedPaymentClearingEventDto (UUID operationToken,
                                            LocalDateTime timestamp,
                                            String detail) {
}
