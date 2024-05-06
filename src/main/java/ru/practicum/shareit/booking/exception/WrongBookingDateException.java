package ru.practicum.shareit.booking.exception;

public class WrongBookingDateException extends RuntimeException{
    public WrongBookingDateException(String message) {
        super(message);
    }

    public WrongBookingDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
