package ru.practicum.shareit.user.exceptions;

public class ConflictUserEmailException extends RuntimeException {
    public ConflictUserEmailException(String message) {
        super(message);
    }
}
