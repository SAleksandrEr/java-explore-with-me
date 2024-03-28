package ru.practicum.exception;

public class DataNotFoundException extends RuntimeException {

    public String reason;

    public DataNotFoundException(String message, String reason) {

        super(message);
        this.reason = reason;
    }
}
