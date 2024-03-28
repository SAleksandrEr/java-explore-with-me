package ru.practicum.compilation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class UpdateCompilationRequest {

    private Set<Long> events;

    private Boolean pinned;

    @Size(min=1,max=50)
    private String title;
}
