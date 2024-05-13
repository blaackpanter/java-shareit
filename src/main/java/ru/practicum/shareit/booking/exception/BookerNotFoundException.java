package ru.practicum.shareit.booking.exception;

public class BookerNotFoundException extends RuntimeException {
    public BookerNotFoundException(String message) {
        super(message);
    }
}
