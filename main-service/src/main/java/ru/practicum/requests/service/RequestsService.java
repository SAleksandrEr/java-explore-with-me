package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepositoryJpa;
import ru.practicum.event.dto.StateEventEnum;
import ru.practicum.event.model.Event;
import ru.practicum.event.storage.EventRepositoryJpa;
import ru.practicum.exception.ConditionsDataException;
import ru.practicum.requests.dto.*;
import ru.practicum.requests.storage.RequestRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class RequestsService {

    private final RequestRepositoryJpa requestRepositoryJpa;

    private final UserRepositoryJpa userRepositoryJpa;

    private final EventRepositoryJpa eventRepositoryJpa;

    private final RequestMapper requestMapper;

    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        Event event = eventRepositoryJpa.findById(eventId).orElseThrow(
                () -> new DataNotFoundException("Event with id=" + eventId + " was not found", "The required object was not found."));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConditionsDataException("Инициатор события не может добавить запрос на участие в своём событии"
                    , "Integrity constraint has been violated.", HttpStatus.CONFLICT);
        }
        if (!event.getState().equals(StateEventEnum.PUBLISHED)) {
            throw new ConditionsDataException("Нельзя участвовать в неопубликованном событии"
                    , "Integrity constraint has been violated.", HttpStatus.CONFLICT);
        }
        if (event.getParticipantLimit() != 0) {
            int limit = requestRepositoryJpa.findByEventIdAndStatus(eventId, StatusEnumRequest.CONFIRMED).size();
            int limitReq = event.getParticipantLimit();
            if (limit >= limitReq) {
                throw new ConditionsDataException("У события достигнут лимит запросов на участие"
                        , "Integrity constraint has been violated.", HttpStatus.CONFLICT);
            }
        }
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setCreated(LocalDateTime.now().withNano(0));
        participationRequest.setEvent(event);
        participationRequest.setRequester(user);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(StatusEnumRequest.CONFIRMED);
        } else {
            participationRequest.setStatus(StatusEnumRequest.PENDING);
        }
        requestRepositoryJpa.save(participationRequest);
        return requestMapper.toParticipationRequestDto(requestRepositoryJpa.save(participationRequest));
    }

    @Transactional
    public ParticipationRequestDto updateRequestId(Long userId, Long requestId) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        ParticipationRequest participationRequest = requestRepositoryJpa.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException("ParticipationRequest with id=" + requestId + " was not found", "The required object was not found."));
        if (userId.equals(participationRequest.getRequester().getId())) {
            participationRequest.setStatus(StatusEnumRequest.CANCELED);
            requestRepositoryJpa.updateRequest(participationRequest.getStatus(), requestId);
        } else {
            throw new ConditionsDataException("User with id=" + userId + " was not owner of the request"
                    , "The required object was not found.", HttpStatus.NOT_FOUND);
        }
        return requestMapper.toParticipationRequestDto(participationRequest);
    }

    public List<ParticipationRequestDto> findRequestsByUserId(Long userId) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        List<ParticipationRequest> listRequestDto = requestRepositoryJpa.findByRequesterId(userId);
        return listRequestDto.stream().map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResult updateRequestsEventId(EventRequestStatusUpdateRequest eventUserRequest, Long userId, Long eventId) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        Event event = eventRepositoryJpa.findById(eventId).orElseThrow(
                () -> new DataNotFoundException("Event with id=" + eventId + " was not found", "The required object was not found."));
        if (event.getParticipantLimit() == 0 || event.getRequestModeration().equals(false)) {
           for (Long reqId : eventUserRequest.getRequestIds()) {
               requestRepositoryJpa.updateRequest(checkStatus(eventUserRequest.getStatus()), reqId);
           }
            return mappingToRequestStatusUpdateResult(requestRepositoryJpa.findByEventIdAndIdIn(eventId, eventUserRequest.getRequestIds()));
        } else {
            List<ParticipationRequest> listRequests = requestRepositoryJpa.findByEventIdAndIdIn(eventId, eventUserRequest.getRequestIds());
            int limit = requestRepositoryJpa.findByEventIdAndStatus(eventId, StatusEnumRequest.CONFIRMED).size();
            int limitReq = event.getParticipantLimit();
            if (limit < limitReq) {
                for (ParticipationRequest request: listRequests) {
                    if (limit > event.getParticipantLimit()) {
                        request.setStatus(StatusEnumRequest.CANCELED);
                    }
                    if (request.getStatus().equals(StatusEnumRequest.PENDING)) {
                        request.setStatus(checkStatus(eventUserRequest.getStatus()));
                        if (request.getStatus().equals(StatusEnumRequest.CONFIRMED)) {
                            limit++;
                        }
                    }
                }
            } else {
                throw new ConditionsDataException("The participant limit has been reached"
                        , "For the requested operation the conditions are not met.", HttpStatus.CONFLICT);
            }
            for (ParticipationRequest reqId : listRequests) {
                requestRepositoryJpa.updateRequest(reqId.getStatus(), reqId.getId());
            }
            return mappingToRequestStatusUpdateResult(listRequests);
        }
    }

    public List<ParticipationRequestDto> findRequestsByEventIdAndUserId(Long userId, Long eventId) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        eventRepositoryJpa.findById(eventId).orElseThrow(
                () -> new DataNotFoundException("Event with id=" + eventId + " was not found", "The required object was not found."));
        return requestRepositoryJpa.findByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    private EventRequestStatusUpdateResult mappingToRequestStatusUpdateResult(List<ParticipationRequest> listRequests) {
        List<ParticipationRequestDto> listRequestDto = listRequests.stream().map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        List<ParticipationRequestDto> confirmedRequests = listRequestDto.stream()
                .filter(requestDto -> requestDto.getStatus().equals(StatusEnumRequest.CONFIRMED.toString()))
                .collect(Collectors.toList());
        List<ParticipationRequestDto> rejectedRequests = listRequestDto.stream()
                .filter(requestDto -> !requestDto.getStatus().equals(StatusEnumRequest.CONFIRMED.toString()))
                .collect(Collectors.toList());
        return EventRequestStatusUpdateResult.builder().rejectedRequests(rejectedRequests).confirmedRequests(confirmedRequests).build();
    }

    private StatusEnumRequest  checkStatus(StatusEnumReqUpdate status) {
        if (status.equals(StatusEnumReqUpdate.CONFIRMED)) {
            return StatusEnumRequest.CONFIRMED;
        } else if (status.equals(StatusEnumReqUpdate.REJECTED)) {
            return StatusEnumRequest.REJECTED;
        } else {
            return StatusEnumRequest.CANCELED;
        }
    }

}
