package ru.practicum.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class EventShortDto implements Comparable<EventShortDto>  {

    @NotBlank
    private String annotation;

    @NotNull
    private CategoryDto category;

    private Long confirmedRequests;

    @NotBlank
    private String eventDate;

    private Long id;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Boolean paid;

    @NotBlank
    private String title;

    private Long views;

    @Override
    public int compareTo(EventShortDto eventShortDto) {
        if (Objects.equals(eventShortDto.getViews(), getViews())) {
            return 0;
        } else if (eventShortDto.getViews() > getViews()) {
            return 1;
        } else {
            return -1;
        }
    }

}
