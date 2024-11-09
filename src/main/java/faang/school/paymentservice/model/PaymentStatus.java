package faang.school.paymentservice.model;

public enum PaymentStatus {
    AUTH_PENDING,
    AUTH_ERROR,
    AUTH_SUCCESS,

    CONFIRM_PENDING,
    CONFIRM_ERROR,
    CONFIRM_SUCCESS,

    CANCEL_PENDING,
    CANCEL_ERROR,
    CANCEL_SUCCESS,

    CLEAR_PENDING,
    CLEAR_ERROR,
    CLEAR_SUCCESS,
}