package faang.school.paymentservice.dto;

public enum PaymentStatus {
    SUCCESS,
    AUTHORIZATION_PENDING,
    AUTHORIZED,
    AUTHORIZATION_FAILED,
    CLEAR_FAILED,
    CANCELLATION_PENDING,
    CANCELED
}
