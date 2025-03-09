package faang.school.paymentservice.exception;

public class PaymentOperationException extends NonRetryableException {
    public PaymentOperationException(String message) {
        super(message);
    }
}
