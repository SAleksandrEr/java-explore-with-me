package ru.practicum.compilation.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.practicum.event.dto.EventShortDto;

import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class CompilationDto {

    private Set<EventShortDto> events;

    private Long id;

    private Boolean pinned;

    private String title;

}
