package faang.school.paymentservice.exception;

public class PaymentAlreadyCreatedException extends RuntimeException {
    public PaymentAlreadyCreatedException(String message) {
        super(message);
    }
}