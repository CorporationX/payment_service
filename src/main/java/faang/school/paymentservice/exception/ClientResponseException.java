package faang.school.paymentservice.exception;

public class ClientResponseException extends RuntimeException {
    public ClientResponseException(String message) {
        super(message);
    }
}