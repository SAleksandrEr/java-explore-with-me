package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "eventDate", source = "newEventDto.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "category.id", source = "newEventDto.category")
    @Mapping(target = "lat", source = "newEventDto.location.lat")
    @Mapping(target = "lon", source = "newEventDto.location.lon")
    Event toEvent(NewEventDto newEventDto);

    @Mapping(target = "eventDate", source = "event.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "createdOn", source = "event.createdOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "publishedOn", source = "event.publishedOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "location.lat", source = "event.lat")
    @Mapping(target = "location.lon", source = "event.lon")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "eventDate", source = "event.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "confirmedRequests", ignore = true)
    EventShortDto toEventShortDto(Event event);
}
