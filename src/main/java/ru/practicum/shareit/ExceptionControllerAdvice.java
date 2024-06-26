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
import ru.practicum.shareit.booking.exception.BookerNotFoundException;
import ru.practicum.shareit.booking.exception.BookingNotAvailableException;
import ru.practicum.shareit.booking.exception.ForbiddenBookingException;
import ru.practicum.shareit.booking.exception.WrongBookerException;
import ru.practicum.shareit.booking.exception.WrongBookingDateException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.WrongBookingStatusException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.WrongCommentDateException;
import ru.practicum.shareit.item.exceptions.WrongOwnerIdException;
import ru.practicum.shareit.request.exception.ItemRequestNotFound;
import ru.practicum.shareit.request.exception.RequesterNotFound;
import ru.practicum.shareit.user.exceptions.ConflictUserEmailException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.Map;

@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.debug("Handle error", ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(value = {WrongOwnerIdException.class})
    protected ResponseEntity<Object> customForbidden(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "You dont have permission. " + ex.getMessage();
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = ConflictUserEmailException.class)
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(final RuntimeException e) {
        return new ResponseEntity<>(Map.of("error", "Unknown state: UNSUPPORTED_STATUS"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(
            value = {UserNotFoundException.class, BookerNotFoundException.class,
                    ItemNotFoundException.class, BookingNotFoundException.class,
                    ForbiddenBookingException.class, WrongBookerException.class,
                    RequesterNotFound.class, ItemRequestNotFound.class
            }
    )
    protected ResponseEntity<Object> customNotFound(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(
            value = {
                    BookingNotAvailableException.class,
                    WrongBookingDateException.class,
                    WrongBookingStatusException.class,
                    WrongCommentDateException.class
            }
    )
    protected ResponseEntity<Object> customBadRequest(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
