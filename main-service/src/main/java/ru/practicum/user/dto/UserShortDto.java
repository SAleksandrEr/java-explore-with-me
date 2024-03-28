package ru.practicum.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class UserShortDto {

    @NotNull
    private Long id;

    @NotBlank
    private String name;
}
