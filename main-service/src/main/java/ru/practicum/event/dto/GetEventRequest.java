package ru.practicum.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class GetEventRequest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    List<Long> users;

    List<StateEventEnum> states;

    List<Long> categories;

    LocalDateTime rangeStart;

    LocalDateTime rangeEnd;

    int from;

    int size;

    public static Pageable pageRequest(Integer from, Integer size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

    public static GetEventRequest of(List<Long> users, List<String> states,  List<Long> categories, String rangeStart, String rangeEnd, int from, int size) {
        GetEventRequest request = new GetEventRequest();
        if (rangeStart != null) {
            LocalDateTime timeStart = LocalDateTime.parse(rangeStart, dateTimeFormatter);
            request.setRangeStart(timeStart);
        }
        if (rangeEnd != null) {
            LocalDateTime timeEnd = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
            request.setRangeEnd(timeEnd);
        }
        request.setFrom(from);
        request.setSize(size);
        if (users != null) {
            request.setUsers(users);
        }
        if (states != null) {
            request.setStates(states.stream().map(StateEventEnum::valueOf).collect(Collectors.toList()));
        }
        if (categories != null) {
            request.setCategories(categories);
        }
        return request;
    }

    public boolean hasUsers() {
        return users != null && !users.isEmpty();
    }

    public boolean hasStates() {
        return states != null && !states.isEmpty();
    }

    public boolean hasCategories() {
        return categories != null && !categories.isEmpty();
    }
}
