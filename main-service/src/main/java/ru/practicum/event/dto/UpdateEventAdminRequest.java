package ru.practicum.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class UpdateEventAdminRequest {

    @Size(min = 20,max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20,max = 7000)
    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    private StateActionAdminEnum stateAction;

    @Size(min = 3,max = 120)
    private String title;

}
