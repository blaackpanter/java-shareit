package ru.practicum.shareit.booking.exception;

public class BookerNotFoundException extends RuntimeException{
    public BookerNotFoundException(String message) {
        super(message);
    }

    public BookerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
