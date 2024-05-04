package ru.practicum.shareit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.shareit.item.exceptions.WrongOwnerIdException;
import ru.practicum.shareit.user.exceptions.ConflictUserEmailException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.debug("Handle error", ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(value = WrongOwnerIdException.class)
    protected ResponseEntity<Object> handleWrongOwnerId(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "You are not an owner. " + ex.getMessage();
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = ConflictUserEmailException.class)
    protected ResponseEntity<Object> handleCOnflict(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    protected ResponseEntity<Object> userNotFound(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}
