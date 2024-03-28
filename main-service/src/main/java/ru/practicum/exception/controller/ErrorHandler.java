package ru.practicum.exception.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.model.ApiError;
import ru.practicum.exception.model.StatusEnum;
import ru.practicum.exception.ConditionsDataException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ErrorHandler {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final DataNotFoundException e) {
        return ApiError.builder().message(e.getMessage()).status(StatusEnum.fromValue("404")).reason(e.reason)
                .timestamp(dateTimeFormatter.format(LocalDateTime.now().withNano(0))).build();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNotFoundException(final ValidationException e) {
        return ApiError.builder().message(e.getMessage()).status(StatusEnum.fromValue("400")).reason(e.reason)
                .timestamp(dateTimeFormatter.format(LocalDateTime.now().withNano(0)))
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return ApiError.builder().message(e.getLocalizedMessage()).status(StatusEnum.fromValue("400")).reason("Incorrectly made request.")
                .timestamp(dateTimeFormatter.format(LocalDateTime.now().withNano(0)))
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        return ApiError.builder().message(e.getLocalizedMessage()).status(StatusEnum.fromValue("400")).reason("Incorrectly made request.")
                .timestamp(dateTimeFormatter.format(LocalDateTime.now().withNano(0)))
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final HttpMessageNotReadableException e) {
        return ApiError.builder().message(e.getLocalizedMessage()).status(StatusEnum.fromValue("400")).reason("Incorrectly made request.")
                .timestamp(dateTimeFormatter.format(LocalDateTime.now().withNano(0)))
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        return ApiError.builder().message(e.getLocalizedMessage()).status(StatusEnum.fromValue("409")).reason("Integrity constraint has been violated.")
                .timestamp(dateTimeFormatter.format(LocalDateTime.now().withNano(0)))
                .build();
    }

    @ExceptionHandler(ConditionsDataException.class)
    public ResponseEntity<Object> handleConditionsDataException(final ConditionsDataException e) {
        return new ResponseEntity<>(ApiError.builder().message(e.getMessage()).status(StatusEnum.fromValue(String.valueOf(e.code.value()))).reason(e.reason)
                .timestamp(dateTimeFormatter.format(LocalDateTime.now().withNano(0)))
                .build(), e.code);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable e) {
        return ApiError.builder().message(e.getLocalizedMessage()).status(StatusEnum.fromValue("500")).reason("Произошла непредвиденная ошибка")
                .timestamp(dateTimeFormatter.format(LocalDateTime.now().withNano(0)))
                .build();
    }

}

