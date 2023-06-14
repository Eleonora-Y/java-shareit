package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleBadRequestException(final BadRequestException e) {
        log.warn("400 {}", e.getMessage());
        return ErrorMessage.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationException(ValidationException e) {
        log.warn("400 {}", e.getMessage(), e);
        return ErrorMessage.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleNotAvailableException(NotAvailableException e) {
        log.warn("400 {}", e.getMessage());
        return ErrorMessage.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleDuplicateEmail(final ConstraintViolationException e) {
        log.warn("409 {}", e.getMessage());
        return ErrorMessage.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleUnknownDataException(NotFoundException e) {
        log.warn("404 {}", e.getMessage());
        return ErrorMessage.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handlerAccessException(final OperationAccessException e) {
        log.warn("404 {}", e.getMessage());
        return ErrorMessage.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleThrowable(final Exception e) {
        log.warn("500 {}", e.getMessage());
        return ErrorMessage.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUnknownDataException(MethodArgumentNotValidException e) {
        log.warn("400 {}", e.getMessage());
        return ErrorMessage.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUnknownDataException(TimeDataException e) {
        log.warn("400 {}", e.getMessage());
        return ErrorMessage.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleThrowable(Throwable throwable) {
        log.error("Unknown error", throwable);
        return ErrorMessage.builder().error(throwable.getMessage()).build();
    }
}
