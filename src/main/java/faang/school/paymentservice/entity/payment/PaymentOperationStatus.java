package faang.school.paymentservice.entity.payment;

public enum PaymentOperationStatus {
    PENDING,
    AUTHORIZED,
    CLEARED,
    CANCELED,
    AUTHORIZATION_FAILED,
    CLEAR_FAILED,
    CANCELLATION_FAILED
    // TODO: если лок занят, то надо вернуть в авторизацию, а не статус ошибки
}
