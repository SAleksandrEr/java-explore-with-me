package ru.practicum.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class NewEventDto {

    @NotBlank
    @Size(min = 20,max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20,max = 7000)
    private String description;

    @NotBlank
    private String eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank
    @Size(min = 3,max = 120)
    private String title;

    public Boolean getPaid() {
        if (paid == null) {
            return paid = false;
        }
        return paid;
    }

    public Integer getParticipantLimit() {
        if (participantLimit == null) {
            return participantLimit = 0;
        }
        return participantLimit;
    }

    public Boolean getRequestModeration() {
        if (requestModeration == null) {
            return requestModeration = true;
        }
        return requestModeration;
    }
}
