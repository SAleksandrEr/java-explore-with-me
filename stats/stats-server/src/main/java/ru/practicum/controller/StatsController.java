package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EventDto;
import ru.practicum.EventDtoResponse;
import ru.practicum.GetEventRequest;
import ru.practicum.service.StatsService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    @Transactional
    @RequestMapping(path = "/hit")
    @PostMapping
    public void createEvent(@RequestBody EventDto eventDto) {
            statsService.createEvent(eventDto);
    }

    @RequestMapping(path = "/stats")
    @GetMapping
    public List<EventDtoResponse> getStatsEvents(@RequestParam(value = "start") String start,
                                                 @RequestParam(value = "end") String end,
                                                 @RequestParam(value = "uris", required = false) List<String> uris,
                                                 @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        return statsService.getStatsEvents(GetEventRequest.of(start,end, uris, unique));
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
