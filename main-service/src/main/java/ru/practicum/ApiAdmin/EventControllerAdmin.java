package ru.practicum.ApiAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.GetEventRequest;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ValidationException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventControllerAdmin {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Object> findEvents(@RequestParam(value = "users", required = false) List<Long> users,
                                               @RequestParam(value = "states", required = false) List<String> states,
                                               @RequestParam(value = "categories", required = false) List<Long> categories,
                                               @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                               @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                               @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                               @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Param - <from> or <size> is not correct", "Incorrectly made request.");
        }

        return new ResponseEntity<>(eventService.findEvents(GetEventRequest.of(users, states, categories, rangeStart, rangeEnd, from, size)), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> updateAdminEventId(@Valid @RequestBody UpdateEventAdminRequest eventAdminRequest, @PathVariable("eventId") Long eventId) {
        return new ResponseEntity<>(eventService.updateAdminEventId(eventAdminRequest, eventId), HttpStatus.OK);
    }
}
