package faang.school.paymentservice.exception;

public class DuplicatePaymentException extends NonRetryableException {
    public DuplicatePaymentException(String message) {
        super(message);
    }
}
