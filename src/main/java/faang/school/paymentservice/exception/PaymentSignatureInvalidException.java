package faang.school.paymentservice.exception;

public class PaymentSignatureInvalidException extends RuntimeException {
    public PaymentSignatureInvalidException(String message) {
        super(message);
    }
}
