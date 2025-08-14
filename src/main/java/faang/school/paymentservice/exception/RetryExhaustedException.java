package faang.school.paymentservice.exception;


import org.slf4j.helpers.MessageFormatter;

public class RetryExhaustedException extends RuntimeException {
    public RetryExhaustedException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }

    public RetryExhaustedException(String messagePattern, Throwable cause, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage(), cause);
    }
}
