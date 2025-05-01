package faang.school.paymentservice.exception;

public class NoHandlerFoundException extends RuntimeException {
    public NoHandlerFoundException(String message) {
        super(message);
    }
}
