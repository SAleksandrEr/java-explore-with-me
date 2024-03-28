package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EventDto;
import ru.practicum.exception.ValidationException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class StatsClientController {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatsClient statsClient;

    @RequestMapping(path = "/hit")
    @PostMapping
    public ResponseEntity<Object> createEvent(@Valid @RequestBody EventDto eventDto) {
        statsClient.createEvent(eventDto);
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @RequestMapping(path = "/stats")
    @GetMapping
    public ResponseEntity<Object> getStatsEvents(@RequestParam(value = "start", required = false) String start,
                                                 @RequestParam(value = "end", required = false) String end,
                                                 @RequestParam(value = "uris", required = false) List<String> uris,
                                                 @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
    if (start != null && end != null) {
        LocalDateTime startDate = LocalDateTime.parse(start, dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse(end, dateTimeFormatter);
        if (startDate.isAfter(endDate) || startDate.equals(endDate)) {
            throw new ValidationException("Invalid data " + startDate + " " + endDate);
        }
    }
        return statsClient.getStatsEvent(start, end, uris, unique);
    }
}
