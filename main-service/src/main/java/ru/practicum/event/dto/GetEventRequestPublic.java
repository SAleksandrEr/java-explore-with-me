package ru.practicum.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GetEventRequestPublic {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    String text;

    LocalDateTime rangeStart;

    LocalDateTime rangeEnd;

    Boolean onlyAvailable;

    SortOption sort;

    List<Long> categories;

    Boolean paid;

    int from;

    int size;

    public static Pageable pageRequest(Integer from, Integer size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

    public static GetEventRequestPublic of(String text, String rangeStart, String rangeEnd, Boolean onlyAvailable, List<Long> categories, Boolean paid, String sort, int from, int size) {
        GetEventRequestPublic request = new GetEventRequestPublic();
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
        request.setOnlyAvailable(onlyAvailable);
        if (paid != null) {
            request.setPaid(paid);
        }
        if (text != null) {
            request.setText(text.toLowerCase());
        }
        if (sort != null) {
            request.setSort(SortOption.valueOf(sort));
        }

        if (categories != null) {
            request.setCategories(categories);
        }
        return request;
    }

    public boolean hasCategories() {
        return categories != null && !categories.isEmpty();
    }
}
