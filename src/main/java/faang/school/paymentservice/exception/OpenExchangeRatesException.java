package faang.school.paymentservice.exception;

public class OpenExchangeRatesException extends RuntimeException {
    public OpenExchangeRatesException(String message) {
        super(message);
    }

    public OpenExchangeRatesException(String message, Throwable cause) {
        super(message, cause);
    }
}
