package ru.practicum.ApiPrivate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.service.RequestsService;


@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestsControllerPrivate {

    private final RequestsService requestsService;

    @PostMapping
    public ResponseEntity<Object> createRequest(@PathVariable("userId") Long userId, @RequestParam(value = "eventId") Long eventId) {
        return new ResponseEntity<>(requestsService.createRequest(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<Object> updateRequestId(@PathVariable("userId") Long userId, @PathVariable("requestId") Long requestId) {
        return new ResponseEntity<>(requestsService.updateRequestId(userId, requestId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> findRequestsByUserId(@PathVariable("userId") Long userId) {
        return new ResponseEntity<>(requestsService.findRequestsByUserId(userId), HttpStatus.OK);
    }
}
