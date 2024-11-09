package faang.school.paymentservice.controller;

import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.exception.EventProcessingException;
import faang.school.paymentservice.exception.EventPublishingException;
import faang.school.paymentservice.exception.NotFoundException;
import faang.school.paymentservice.exception.OperationNotPermittedException;
import faang.school.paymentservice.model.dto.ErrorResponse;
import faang.school.paymentservice.model.enums.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), ""))
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String message = e.getMessage().contains("Currency") ? "We only accept " + Arrays.toString(Currency.values())
                : e.getMessage();

        return new ErrorResponse(message);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception occurred", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.error("Not found exception occurred", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument exception occurred", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleOperationNotPermittedException(OperationNotPermittedException e) {
        log.error("Illegal argument exception occurred", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException ex) {
        log.error("Data Validation Error", ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(EventProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleEventProcessingException(EventProcessingException ex) {
        log.error("Event Processing Exception", ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(EventPublishingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleEventPublishingException(EventPublishingException ex) {
        log.error("Event Publishing Exception", ex);
        return new ErrorResponse(ex.getMessage());
    }
}
