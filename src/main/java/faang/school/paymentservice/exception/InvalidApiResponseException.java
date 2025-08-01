package faang.school.paymentservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class InvalidApiResponseException extends RuntimeException {
    public InvalidApiResponseException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
