package ru.practicum.exception.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class ApiError {

    private StatusEnum status;

    private String reason;

    private String message;

    private String timestamp;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor(force = true)
    public static class Errors {
        private StatusEnum status;

        private String reason;

        private String message;

        private String timestamp;

        private List<String> errors;
    }

}
