package ru.practicum.category.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class CategoryDto {

    private Long id;

    @NotBlank
    @Size(max = 50)
    private String name;
}
