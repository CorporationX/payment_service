package faang.school.paymentservice.exception;

import faang.school.paymentservice.dto.ErrorType;
import org.slf4j.helpers.MessageFormatter;

public class EmptyApiResponseException extends RuntimeException {
    public EmptyApiResponseException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }

    public EmptyApiResponseException(ErrorType errorType) {
        super(errorType.getMessage());
    }
}
