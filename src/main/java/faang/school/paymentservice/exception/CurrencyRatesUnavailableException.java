package faang.school.paymentservice.exception;

public class CurrencyRatesUnavailableException extends RuntimeException {
    public CurrencyRatesUnavailableException(String message) {
        super(message);
    }

    public CurrencyRatesUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
