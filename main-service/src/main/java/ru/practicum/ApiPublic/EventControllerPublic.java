package ru.practicum.ApiPublic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.practicum.controller.StatsClient;
import ru.practicum.exception.ValidationException;
import ru.practicum.event.dto.GetEventRequestPublic;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConditionsDataException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventControllerPublic {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventService eventService;

    private final StatsClient client;


    @GetMapping
    public ResponseEntity<Object> findEventsPublic(@RequestParam(value = "text", required = false) String text,
                                             @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                             @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                             @RequestParam(value = "onlyAvailable", required = false, defaultValue = "false") Boolean onlyAvailable,
                                             @RequestParam(value = "categories", required = false) List<Long> categories,
                                             @RequestParam(value = "paid", required = false) Boolean paid,
                                             @RequestParam(value = "sort", required = false) String sort,
                                             @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                             @RequestParam(value = "size", required = false, defaultValue = "10") int size, HttpServletRequest request) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Param - <from> or <size> is not correct", "Incorrectly made request.");
        }
        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime startDate = LocalDateTime.parse(rangeStart, dateTimeFormatter);
            LocalDateTime endDate = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
            if (startDate.isAfter(endDate) || startDate.equals(endDate)) {
                throw new ConditionsDataException("Field: rangeStart or rangeEnd. Value: " + rangeStart + " " + rangeEnd, "For the requested operation the conditions are not met.",
                        HttpStatus.BAD_REQUEST);
            }
        }
        GetEventRequestPublic getEventRequestPublic = GetEventRequestPublic.of(text, rangeStart, rangeEnd, onlyAvailable, categories, paid, sort, from, size);
        return new ResponseEntity<>(eventService.findEventsPublic(getEventRequestPublic, request, client), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findEventsPublicId(@PathVariable("id") Long id, HttpServletRequest request) {
        return new ResponseEntity<>(eventService.findEventsPublicId(id, request, client), HttpStatus.OK);
    }
}
