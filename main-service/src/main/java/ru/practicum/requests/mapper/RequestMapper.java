package ru.practicum.requests.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "event", source = "participationRequest.event.id")
    @Mapping(target = "requester", source = "participationRequest.requester.id")
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);
}
