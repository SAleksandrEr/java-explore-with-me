package ru.practicum.compilation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class NewCompilationDto {

    private Set<Long> events;

    private Boolean pinned;

    @NotBlank
    @Size(min = 1,max = 50)
    private String title;

    public Boolean getPinned() {
        if (pinned == null) {
            return pinned = false;
        }
        return pinned;
    }
}
