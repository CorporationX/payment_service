package faang.school.paymentservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
