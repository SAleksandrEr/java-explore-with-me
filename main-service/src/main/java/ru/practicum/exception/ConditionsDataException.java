package ru.practicum.exception;

import org.springframework.http.HttpStatus;

public class ConditionsDataException extends RuntimeException {

    public String reason;

    public HttpStatus code;
    public ConditionsDataException(String message, String reason, HttpStatus code) {
        super(message);
        this.reason = reason;
        this.code = code;
    }
}
