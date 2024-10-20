package faang.school.paymentservice.exception.api;

public class APIConversionRatesException extends RuntimeException {
    public APIConversionRatesException(String message, Throwable error) {
        super(message);
    }
}
