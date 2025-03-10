package faang.school.paymentservice.exception;

public class NoSuchExchangeRateException extends RuntimeException {
    public NoSuchExchangeRateException(String message) {
        super(message);
    }
}
