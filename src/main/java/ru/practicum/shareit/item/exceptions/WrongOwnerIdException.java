package ru.practicum.shareit.item.exceptions;

public class WrongOwnerIdException extends RuntimeException {
    public WrongOwnerIdException(String message) {
        super(message);
    }

    public WrongOwnerIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
