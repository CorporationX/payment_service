package faang.school.paymentservice.exception;

public class PaymentNotAuthException extends RuntimeException {
    public PaymentNotAuthException(String message) {
        super(message);
    }
}
