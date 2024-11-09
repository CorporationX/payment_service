package faang.school.paymentservice.exception;

public class EventProcessingException extends RuntimeException {
    public EventProcessingException(String message) {
        super(message);
    }
}