package faang.school.paymentservice.entity.payment;

public enum PaymentOperationStatus {
    PENDING,
    AUTHORIZED,
    CLEARED,
    CANCELED,
    AUTHORIZATION_FAILED,
    CLEAR_FAILED,
    CANCELLATION_FAILED
}
