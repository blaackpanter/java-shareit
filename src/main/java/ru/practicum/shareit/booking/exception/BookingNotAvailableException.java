package ru.practicum.shareit.booking.exception;

public class BookingNotAvailableException extends RuntimeException {
    public BookingNotAvailableException(String message) {
        super(message);
    }

    public BookingNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
