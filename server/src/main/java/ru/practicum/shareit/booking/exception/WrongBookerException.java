package ru.practicum.shareit.booking.exception;

public class WrongBookerException extends RuntimeException {
    public WrongBookerException(String message) {
        super(message);
    }
}
