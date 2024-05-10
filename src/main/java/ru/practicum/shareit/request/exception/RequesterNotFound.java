package ru.practicum.shareit.request.exception;

public class RequesterNotFound extends RuntimeException {
    public RequesterNotFound(String message) {
        super(message);
    }

    public RequesterNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
