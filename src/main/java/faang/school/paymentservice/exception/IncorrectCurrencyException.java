package faang.school.paymentservice.exception;

public class IncorrectCurrencyException extends RuntimeException {
    public IncorrectCurrencyException(String message) {
        super(message);
    }
}
