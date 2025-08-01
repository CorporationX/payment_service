package faang.school.paymentservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class ApiRequestException extends RuntimeException {
    public ApiRequestException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
