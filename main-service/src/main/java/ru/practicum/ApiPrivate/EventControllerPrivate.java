package ru.practicum.ApiPrivate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.GetEventRequest;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.exception.ConditionsDataException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.service.RequestsService;
import ru.practicum.event.service.EventService;
import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventControllerPrivate {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventService eventService;

    private final RequestsService requestsService;

    @PostMapping
    public ResponseEntity<Object> createEvent(@Valid @RequestBody NewEventDto newEventDto, @PathVariable("userId") Long userId) {
        LocalDateTime currant = LocalDateTime.now();
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), dateTimeFormatter);
        if (eventDate.isBefore(currant.plusHours(2))) {
            throw new ConditionsDataException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + eventDate
                    , "For the requested operation the conditions are not met.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(eventService.createEvent(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Object> findEventsByUserId(@PathVariable("userId") Long userId, @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                 @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Param - <from> or <size> is not correct", "Incorrectly made request.");
        }
        return new ResponseEntity<>(eventService.findEventsByUserId(userId, GetEventRequest.pageRequest(from, size)), HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Object> findEventByUserIdAndEventId(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        return new ResponseEntity<>(eventService.findEventByUserIdAndEventId(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> updateEventId(@Valid @RequestBody UpdateEventUserRequest eventUserRequest, @PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        return new ResponseEntity<>(eventService.updateEventId(eventUserRequest, userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<Object> updateRequestsEventId(@Valid @RequestBody EventRequestStatusUpdateRequest eventUserRequest, @PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        return new ResponseEntity<>(requestsService.updateRequestsEventId(eventUserRequest, userId, eventId), HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<Object> findRequestsByEventIdAndUserId(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        return new ResponseEntity<>(requestsService.findRequestsByEventIdAndUserId(userId, eventId), HttpStatus.OK);
    }
}
