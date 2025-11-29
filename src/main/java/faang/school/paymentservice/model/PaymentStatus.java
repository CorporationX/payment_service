package faang.school.paymentservice.model;

public enum PaymentStatus {
    ON_AUTHORIZATION,
    AUTHORIZATION_SUCCESS,
    AUTHORIZATION_ERROR,
    ON_CLEARING,
    CLEARING_SUCCESS,
    CLEARING_ERROR,
    ON_CANCELLING,
    CANCEL_SUCCESS,
    CANCEL_ERROR,
    SERVER_ERROR
}