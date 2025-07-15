package faang.school.paymentservice.event.payment;

import java.util.UUID;

public record PaymentClearingEventDto (UUID operationToken, long userId) {
}
