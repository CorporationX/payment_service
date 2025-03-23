package faang.school.paymentservice.exception;

public class PaymentNotFoundException extends NonRetryableException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
