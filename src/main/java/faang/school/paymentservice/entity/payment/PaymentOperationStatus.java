package faang.school.paymentservice.entity.payment;

public enum PaymentOperationStatus {
    PENDING,
    AUTHORIZED,
    CLEARED,
    CANCELLED,
    FAILED
    // TODO: если лок занят, то надо вернуть в авторизацию, а не статус ошибки
}
