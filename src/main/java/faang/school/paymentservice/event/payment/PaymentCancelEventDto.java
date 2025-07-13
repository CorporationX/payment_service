package faang.school.paymentservice.event.payment;

import java.util.UUID;

public record PaymentCancelEventDto(UUID operationToken, long userId) {
}
