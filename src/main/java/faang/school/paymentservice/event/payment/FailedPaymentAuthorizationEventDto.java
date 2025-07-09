package faang.school.paymentservice.event.payment;

import java.util.UUID;


public record FailedPaymentAuthorizationEventDto (UUID operationToken) {
}

