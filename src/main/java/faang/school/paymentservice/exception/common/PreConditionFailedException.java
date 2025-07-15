package faang.school.paymentservice.exception.common;

public class PreConditionFailedException extends RuntimeException {
    public PreConditionFailedException(String message) {
        super(message);
    }
}
