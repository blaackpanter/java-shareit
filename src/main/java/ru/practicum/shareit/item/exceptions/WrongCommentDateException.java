package ru.practicum.shareit.item.exceptions;

public class WrongCommentDateException extends RuntimeException {
    public WrongCommentDateException(String message) {
        super(message);
    }

    public WrongCommentDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
