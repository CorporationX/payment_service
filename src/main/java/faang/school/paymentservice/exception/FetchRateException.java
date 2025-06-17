package faang.school.paymentservice.exception;

public class FetchRateException extends RuntimeException {
    public FetchRateException(String message) {
        super(message);
    }

    public FetchRateException(String message, Throwable cause) {
        super(message, cause);
    }
}
