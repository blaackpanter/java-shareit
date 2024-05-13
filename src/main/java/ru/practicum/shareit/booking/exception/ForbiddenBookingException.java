package ru.practicum.shareit.booking.exception;

public class ForbiddenBookingException extends RuntimeException {
    public ForbiddenBookingException(String message) {
        super(message);
    }
}
