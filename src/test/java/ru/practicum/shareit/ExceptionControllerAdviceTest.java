package ru.practicum.shareit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionControllerAdviceTest {
    private static final ExceptionControllerAdvice exceptionControllerAdvice = new ExceptionControllerAdvice();

    @Test
    void customForbidden() {
        assertNotNull(exceptionControllerAdvice.customForbidden(new RuntimeException("test"), null));
    }

    @Test
    void handleConflict() {
        assertNotNull(exceptionControllerAdvice.handleConflict(new RuntimeException("test"), null));
    }

    @Test
    void handleMethodArgumentTypeMismatchException() {
        assertNotNull(exceptionControllerAdvice.handleMethodArgumentTypeMismatchException(new RuntimeException("test")));
    }

    @Test
    void customNotFound() {
        assertNotNull(exceptionControllerAdvice.customNotFound(new RuntimeException("test"), null));
    }

    @Test
    void customBadRequest() {
        assertNotNull(exceptionControllerAdvice.customBadRequest(new RuntimeException("test"), null));
    }
}