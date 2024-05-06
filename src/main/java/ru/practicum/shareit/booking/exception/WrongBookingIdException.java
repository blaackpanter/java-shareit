package ru.practicum.shareit.booking.exception;

public class WrongBookingIdException extends RuntimeException {
    public WrongBookingIdException(String message) {
        super(message);
    }

    public WrongBookingIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
