package faang.school.paymentservice.exception;

public class CurrencyRateException extends RuntimeException {
    public CurrencyRateException(String message, Throwable cause) {
        super(message, cause);
    }
}