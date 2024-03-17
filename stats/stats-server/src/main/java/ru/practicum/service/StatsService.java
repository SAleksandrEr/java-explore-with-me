package ru.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.GetEventRequest;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.model.QEvent;
import ru.practicum.storage.EventRepositoryJpa;
import ru.practicum.EventDto;
import ru.practicum.EventDtoResponse;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class StatsService {

    private final EventMapper eventMapper;

    private final EventRepositoryJpa eventRepositoryJpa;

    @Transactional
    public void createEvent(EventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);
        eventRepositoryJpa.save(event);
    }

    public List<EventDtoResponse> getStatsEvents(GetEventRequest request) {
    QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
    if (request.hasUris()) {
            conditions.add(event.uri.in(request.getUris()));
    }
    conditions.add(event.timestamp.after(request.getStart()));
    conditions.add(event.timestamp.before(request.getEnd()));
        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
        Sort sort = Sort.by("ip");
        PageRequest pageRequest = PageRequest.of(0, 20, sort);
        List<Event> eventList = eventRepositoryJpa.findAll(finalCondition, pageRequest)
                .stream().collect(Collectors.toList());
        if (request.getUnique().equals(true)) {
            Map<String, Event> eventListMap = eventList.stream().collect(Collectors
                    .toMap(event1 -> event1.getIp() + event1.getUri(), Function.identity(), (existing, replacement) -> existing));
            return countHitsDistinct(eventListMap.values().stream()
                    .map(eventMapper::toEventDtoResponse).collect(Collectors.toList()));
            } else {
            return countHitsDistinct(eventList.stream()
                    .map(eventMapper::toEventDtoResponse).collect(Collectors.toList()));
        }
    }

    private List<EventDtoResponse> countHitsDistinct(List<EventDtoResponse> eventDtoResponse) {
        eventDtoResponse
                    .forEach(uriEvent -> uriEvent.setHits(eventDtoResponse.stream().filter(count -> uriEvent.getUri().equals(count.getUri())).count()));
        Map<String, EventDtoResponse> eventListMap = eventDtoResponse.stream().collect(Collectors
                .toMap(EventDtoResponse::getUri, Function.identity(), (existing, replacement) -> existing));

        return eventListMap.values().stream().sorted().collect(Collectors.toList());
    }
}
