package faang.school.paymentservice.exception;

import faang.school.paymentservice.dto.ErrorType;
import org.slf4j.helpers.MessageFormatter;

public class InvalidApiResponseException extends RuntimeException {
    public InvalidApiResponseException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }

    public InvalidApiResponseException(ErrorType errorType) {
        super(errorType.getMessage());
    }
}
