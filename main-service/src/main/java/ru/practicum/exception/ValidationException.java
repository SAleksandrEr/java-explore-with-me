package ru.practicum.exception;

public class ValidationException extends RuntimeException {
    public String reason;

    public ValidationException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}

