package ru.practicum.requests.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class EventRequestStatusUpdateResult {

    private List<ParticipationRequestDto> confirmedRequests;

    private List<ParticipationRequestDto> rejectedRequests;

}
