package faang.school.paymentservice.exception;

public class NotEnoughMoneyOnBalanceException extends RuntimeException {
    public NotEnoughMoneyOnBalanceException(String message) {
        super(message);
    }
}
