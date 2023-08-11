package ru.practicum.shareit.exception;

public class ValidationException extends NullPointerException {
    public ValidationException(String message) {
        super(message);
    }
}