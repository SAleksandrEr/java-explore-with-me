package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.EventDto;
import ru.practicum.EventDtoResponse;
import ru.practicum.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "timestamp", source = "eventDto.timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event toEvent(EventDto eventDto);

    EventDtoResponse toEventDtoResponse(Event events);
}
