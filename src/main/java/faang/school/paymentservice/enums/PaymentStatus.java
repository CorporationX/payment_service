package faang.school.paymentservice.enums;

public enum PaymentStatus {
    NEW,
    AUTHORIZED,
    PROCESS_OF_CANCELLATION,
    CANCELLED,
    PROCESS_OF_CLEARING,
    AUTHORIZED_SEND_ERROR,
    CLEARED,
    ERROR_NO_ENOUGH_MONEY,
    ERROR_UNCORRECTED_CURRENCY,
    ERROR
}
