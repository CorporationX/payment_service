package faang.school.paymentservice.exception.common;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }
}
