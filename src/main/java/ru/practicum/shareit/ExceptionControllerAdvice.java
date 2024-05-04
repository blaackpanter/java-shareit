package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.shareit.item.exceptions.WrongOwnerIdException;

@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = WrongOwnerIdException.class)
    protected ResponseEntity<Object> handleWrongOwnerId(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "You are not an owner. " + ex.getMessage();
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.FORBIDDEN);
    }
}
