package faang.school.paymentservice.exception;

/**
 * @author Evgenii Malkov
 */
public class PaymentException extends RuntimeException {

    public PaymentException(String message) {
        super(message);
    }
}
