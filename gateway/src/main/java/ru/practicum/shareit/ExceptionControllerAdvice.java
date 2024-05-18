package ru.practicum.shareit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.debug("Handle error", ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(final RuntimeException e) {
        return new ResponseEntity<>(Map.of("error", "Unknown state: UNSUPPORTED_STATUS"), HttpStatus.BAD_REQUEST);
    }
}
