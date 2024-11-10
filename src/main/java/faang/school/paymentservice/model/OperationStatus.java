package faang.school.paymentservice.model;

public enum OperationStatus {
    PENDING,
    AUTHORIZATION,
    CANCELLATION,
    CLEARING,
    ERROR,
    FINISHED_CLEARING,
    FINISHED_CANCELLATION,
    FINISHED_ERROR
}