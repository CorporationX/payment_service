package faang.school.paymentservice.event;

import faang.school.paymentservice.model.PaymentOperation;

public record PaymentEvent(PaymentOperation operation) {
}
