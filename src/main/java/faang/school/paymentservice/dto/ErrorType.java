package faang.school.paymentservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    SUCCESS_FALSE_ERROR_NULL("Field 'success' is false, but error is null", HttpStatus.CONFLICT),
    EMPTY_RESPONSE_BODY("Empty response body from API", HttpStatus.NOT_FOUND)
    ;

    private final String message;
    private final HttpStatus httpStatus;
}
