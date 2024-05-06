package ru.practicum.shareit.booking.exception;

public class WrongBookingStatusException extends RuntimeException {
    public WrongBookingStatusException(String message) {
        super(message);
    }

    public WrongBookingStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
