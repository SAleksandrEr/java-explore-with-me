package ru.practicum.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class EventFullDto {

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    private String createdOn;

    private String description;

    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private String publishedOn;

    private Boolean requestModeration;

    private StateEventEnum state;

    private String title;

    private Long views;

}
