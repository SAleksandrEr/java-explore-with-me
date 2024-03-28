package ru.practicum.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class Location {

    @NotBlank
    private Float lat;

    @NotBlank
    private Float lon;
}
