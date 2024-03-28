package ru.practicum.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EventDto;
import ru.practicum.EventDtoResponse;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepositoryJpa;
import ru.practicum.controller.StatsClient;
import ru.practicum.event.storage.EventRepositoryJpa;
import ru.practicum.exception.ConditionsDataException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.requests.dto.StatusEnumRequest;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.storage.RequestRepositoryJpa;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepositoryJpa;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class EventService {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepositoryJpa eventRepositoryJpa;

    private final EventMapper eventMapper;

    private final UserRepositoryJpa userRepositoryJpa;

    private final CategoryRepositoryJpa categoryRepositoryJpa;

    private final RequestRepositoryJpa requestRepositoryJpa;

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        Category category = categoryRepositoryJpa.findById(newEventDto.getCategory())
                .orElseThrow(() -> new DataNotFoundException("Category with id=" + newEventDto.getCategory() + " was not found", "The required object was not found."));
        Event event = eventMapper.toEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().withNano(0).format(dateTimeFormatter), dateTimeFormatter));
        event.setState(StateEventEnum.PENDING);
        return eventMapper.toEventFullDto(eventRepositoryJpa.save(event));
    }

    public List<EventFullDto> findEventsByUserId(Long userId, Pageable page) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        QEvent event = QEvent.event;
        BooleanExpression condition = event.initiator.id.eq(userId);
        Sort sort = Sort.by("id");
        PageRequest pageRequest = PageRequest.of(page.getPageNumber(), page.getPageSize(), sort);
        List<Event> eventList = eventRepositoryJpa.findAll(condition, pageRequest)
                .stream().collect(Collectors.toList());
        return eventList.stream().map(eventMapper::toEventFullDto).collect(Collectors.toList());
    }

    public EventFullDto findEventByUserIdAndEventId(Long userId, Long eventId) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        Event event = eventRepositoryJpa.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new DataNotFoundException("Event with id=" + eventId + " was not found", "The required object was not found."));
        return eventMapper.toEventFullDto(event);
    }

    @Transactional
    public EventFullDto updateEventId(UpdateEventUserRequest eventUserRequest, Long userId, Long eventId) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        Event event = eventRepositoryJpa.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new DataNotFoundException("Event with id=" + eventId + " was not found", "The required object was not found."));
        if (event.getState().equals(StateEventEnum.PENDING) || event.getState().equals(StateEventEnum.CANCELED)) {
            if (eventUserRequest.getAnnotation() != null) {
                event.setAnnotation(eventUserRequest.getAnnotation());
            }
            if (eventUserRequest.getCategory() != null) {
                event.setCategory(categoryRepositoryJpa.findById(eventUserRequest.getCategory())
                        .orElseThrow(() -> new DataNotFoundException("Category with id=" + eventUserRequest.getCategory() + " was not found", "The required object was not found.")));
            }
            if (eventUserRequest.getDescription() != null) {
                event.setDescription(eventUserRequest.getDescription());
            }
            if (eventUserRequest.getEventDate() != null) {
                LocalDateTime currant = LocalDateTime.now();
                LocalDateTime eventDate = LocalDateTime.parse(eventUserRequest.getEventDate(), dateTimeFormatter);
                if (eventDate.isBefore(currant.plusHours(2))) {
                    throw new ConditionsDataException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + eventDate,
                            "For the requested operation the conditions are not met.", HttpStatus.BAD_REQUEST);
                }
                event.setEventDate(eventDate);
            }
            if (eventUserRequest.getLocation() != null) {
                if (eventUserRequest.getLocation().getLat() != null) {
                    event.setLat(eventUserRequest.getLocation().getLat());
                }
                if (eventUserRequest.getLocation().getLon() != null) {
                    event.setLat(eventUserRequest.getLocation().getLon());
                }
            }
            if (eventUserRequest.getPaid() != null) {
                event.setPaid(eventUserRequest.getPaid());
            }
            if (eventUserRequest.getParticipantLimit() != null) {
                event.setParticipantLimit(eventUserRequest.getParticipantLimit());
            }
            if (eventUserRequest.getRequestModeration() != null) {
                event.setRequestModeration(eventUserRequest.getRequestModeration());
            }
            if (eventUserRequest.getStateAction() != null) {
                if (eventUserRequest.getStateAction().equals(StateActionEnum.CANCEL_REVIEW)) {
                    event.setState(StateEventEnum.CANCELED);
                }
                if (eventUserRequest.getStateAction().equals(StateActionEnum.SEND_TO_REVIEW)) {
                    event.setState(StateEventEnum.PENDING);
                }
            }
            if (eventUserRequest.getTitle() != null) {
                event.setTitle(eventUserRequest.getTitle());
            }
            eventRepositoryJpa.save(event);
        } else {
            throw new ConditionsDataException("Only pending or canceled events can be changed",
                    "For the requested operation the conditions are not met.", HttpStatus.CONFLICT);
        }
        return eventMapper.toEventFullDto(event);
    }

    public List<EventFullDto> findEvents(GetEventRequest request) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        if (request.hasUsers()) {
            conditions.add(event.initiator.id.in(request.getUsers()));
        }
        if (request.hasStates()) {
            conditions.add(event.state.in(request.getStates()));
        }

        if (request.hasCategories()) {
            conditions.add(event.category.id.in(request.getCategories()));
        }
        if (request.getRangeStart() != null) {
            conditions.add(event.eventDate.after(request.getRangeStart()));
        }
        if (request.getRangeEnd() != null) {
            conditions.add(event.eventDate.before(request.getRangeEnd()));
        }
        Optional<BooleanExpression> finalCondition = conditions.stream()
                .reduce(BooleanExpression::and);
        Sort sort = Sort.by("id");
        PageRequest pageRequest = PageRequest.of(request.getFrom(), request.getSize(), sort);
        List<Event> eventList = eventRepositoryJpa.findAll(Objects.requireNonNull(finalCondition.orElse(event.isNotNull())), pageRequest)
                .stream().collect(Collectors.toList());
        List<EventFullDto> eventFullDto = eventList.stream().map(eventMapper::toEventFullDto).collect(Collectors.toList());
        return countConfirmedRequests(eventFullDto);
    }

    @Transactional
    public EventFullDto updateAdminEventId(UpdateEventAdminRequest eventAdminRequest, Long eventId) {
        Event event = eventRepositoryJpa.findById(eventId).orElseThrow(
                () -> new DataNotFoundException("Event with id=" + eventId + " was not found", "The required object was not found."));
            if (eventAdminRequest.getAnnotation() != null) {
                event.setAnnotation(eventAdminRequest.getAnnotation());
            }
            if (eventAdminRequest.getCategory() != null) {
                event.setCategory(categoryRepositoryJpa.findById(eventAdminRequest.getCategory())
                        .orElseThrow(() -> new DataNotFoundException("Category with id=" + eventAdminRequest.getCategory() + " was not found", "The required object was not found.")));
            }
            if (eventAdminRequest.getDescription() != null) {
                event.setDescription(eventAdminRequest.getDescription());
            }
            if (eventAdminRequest.getEventDate() != null) {
                LocalDateTime currant = LocalDateTime.now();
                LocalDateTime eventDate = LocalDateTime.parse(eventAdminRequest.getEventDate(), dateTimeFormatter);
                if (eventDate.isBefore(currant.plusHours(2))) {
                    throw new ConditionsDataException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + eventDate,
                            "For the requested operation the conditions are not met.", HttpStatus.BAD_REQUEST);
                }
                if (event.getState().equals(StateEventEnum.PUBLISHED) && event.getPublishedOn().isBefore(eventDate.plusHours(1))) {
                    throw new ConditionsDataException("Cannot publish the event because it's not in the right state: PUBLISHED",
                            "For the requested operation the conditions are not met.", HttpStatus.CONFLICT);
                }
                event.setEventDate(eventDate);
            }
            if (eventAdminRequest.getLocation() != null) {
                if (eventAdminRequest.getLocation().getLat() != null) {
                    event.setLat(eventAdminRequest.getLocation().getLat());
                }
                if (eventAdminRequest.getLocation().getLon() != null) {
                    event.setLat(eventAdminRequest.getLocation().getLon());
                }
            }
            if (eventAdminRequest.getPaid() != null) {
                event.setPaid(eventAdminRequest.getPaid());
            }
            if (eventAdminRequest.getParticipantLimit() != null) {
                event.setParticipantLimit(eventAdminRequest.getParticipantLimit());
            }
            if (eventAdminRequest.getRequestModeration() != null) {
                event.setRequestModeration(eventAdminRequest.getRequestModeration());
            }
            if (eventAdminRequest.getStateAction() != null) {
                if (eventAdminRequest.getStateAction().equals(StateActionAdminEnum.REJECT_EVENT) && !event.getState().equals(StateEventEnum.PUBLISHED)) {
                    event.setState(StateEventEnum.CANCELED);
                } else if (eventAdminRequest.getStateAction().equals(StateActionAdminEnum.PUBLISH_EVENT) && event.getState().equals(StateEventEnum.PENDING)) {
                    event.setState(StateEventEnum.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.parse(LocalDateTime.now().withNano(0).format(dateTimeFormatter), dateTimeFormatter));
                } else {
                    throw new ConditionsDataException("Cannot publish the event because it's not in the right state: PUBLISHED",
                            "For the requested operation the conditions are not met.", HttpStatus.CONFLICT);
                }
            }
            if (eventAdminRequest.getTitle() != null) {
                event.setTitle(eventAdminRequest.getTitle());
            }
        eventRepositoryJpa.save(event);
        return eventMapper.toEventFullDto(event);
    }

    public List<EventShortDto> findEventsPublic(GetEventRequestPublic getEventRequest, HttpServletRequest request, StatsClient client) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(event.state.eq(StateEventEnum.PUBLISHED));
        if (getEventRequest.getText() != null) {
            conditions.add(event.annotation.toLowerCase().contains(getEventRequest.getText())
                    .or(event.description.toLowerCase().contains(getEventRequest.getText())));
        }
        if (getEventRequest.getRangeStart() != null) {
            conditions.add(event.eventDate.after(getEventRequest.getRangeStart()));
        }
        if (getEventRequest.getRangeEnd() != null) {
            conditions.add(event.eventDate.before(getEventRequest.getRangeEnd()));
        }
        if (getEventRequest.getRangeStart() == null && getEventRequest.getRangeEnd() == null) {
            conditions.add(event.eventDate.after(LocalDateTime.now().withNano(0)));
        }
        if (getEventRequest.hasCategories()) {
            conditions.add(event.category.id.in(getEventRequest.getCategories()));
        }
        if (getEventRequest.getPaid() != null) {
            conditions.add(event.paid.eq(getEventRequest.getPaid()));
        }
        Sort sort = Sort.by("id");
        if (getEventRequest.getSort() != null && getEventRequest.getSort().equals(SortOption.EVENT_DATE)) {
            sort = Sort.by("eventDate");
        }
        Optional<BooleanExpression> finalCondition = conditions.stream()
                .reduce(BooleanExpression::and);
        PageRequest pageRequest = PageRequest.of(getEventRequest.getFrom(), getEventRequest.getSize(), sort);
        List<Event> eventList = eventRepositoryJpa.findAll(Objects.requireNonNull(finalCondition.orElse(event.isNotNull())), pageRequest)
                .stream().collect(Collectors.toList());
        List<EventShortDto> listEventShort = eventList.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
        List<ParticipationRequest> requestsEvents = requestRepositoryJpa.findByStatusAndEventIdIn(StatusEnumRequest.CONFIRMED,
                listEventShort.stream().map(EventShortDto::getId).collect(Collectors.toSet()));
        listEventShort.forEach(eventShort -> eventShort.setConfirmedRequests(requestsEvents.stream()
                .filter(count -> eventShort.getId().equals(count.getEvent().getId())).count()));
        if (getEventRequest.getOnlyAvailable().equals(true)) {
            listEventShort = listEventShort.stream().filter(eventShort -> {
               for (Event eventDto : eventList) {
                   if (eventShort.getId().equals(eventDto.getId())) {
                       if (eventDto.getParticipantLimit() >= eventShort.getConfirmedRequests()) {
                           return true;
                       }
                       if (eventDto.getParticipantLimit() == 0) {
                           return true;
                       }
                   }
               }
                        return false;
                    }).collect(Collectors.toList());
            }
        List<EventShortDto> listEventShortDto = countViews(listEventShort, request.getRequestURI(), client);
        if (getEventRequest.getSort() != null && getEventRequest.getSort().equals(SortOption.VIEWS)) {
            listEventShortDto = listEventShortDto.stream().sorted().collect(Collectors.toList());
        }
            saveStatistics(request, client);
            return listEventShortDto;
    }

    public EventFullDto findEventsPublicId(Long id, HttpServletRequest request, StatsClient client) {
        Event event = eventRepositoryJpa.findByIdAndState(id, StateEventEnum.PUBLISHED).orElseThrow(
                () -> new DataNotFoundException("Event with id=" + id + " was not found", "The required object was not found."));
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        List<ParticipationRequest> requestsEvents = requestRepositoryJpa.findByEventIdAndStatus(eventFullDto.getId(),StatusEnumRequest.CONFIRMED);
        eventFullDto.setConfirmedRequests(requestsEvents.stream()
                .filter(count -> eventFullDto.getId().equals(count.getEvent().getId())).count());
        EventFullDto finalEventFullDto = countViewsEventFullDtoId(eventFullDto,request.getRequestURI(), client);
        saveStatistics(request, client);
        return finalEventFullDto;
    }

    private List<EventShortDto> countViews(List<EventShortDto> eventShortDto, String uri, StatsClient client) {
     List<String> uris = eventShortDto.stream().map(eventShort -> uri + "/" + eventShort.getId().toString()).collect(Collectors.toList());
     ResponseEntity<Object> responseEntity = client.getStatsEvent(null, null, uris, true);
     if (responseEntity.getStatusCode().is2xxSuccessful()) {
         Object objects = responseEntity.getBody();
         assert objects != null;
         ObjectMapper mapper = new ObjectMapper();
         List<EventDtoResponse> listEvent = mapper.convertValue(objects, new TypeReference<>() {
         });
         if (listEvent.size() > 0) {
             for (EventShortDto eventShort : eventShortDto) {
                 for (EventDtoResponse event : listEvent) {
                     if (event.getUri().equals(uri + "/" + eventShort.getId().toString())) {
                         eventShort.setViews(event.getHits());
                     }
                 }
             }
         } else {
             eventShortDto = eventShortDto.stream().peek(eventShort -> eventShort.setViews(0L)).collect(Collectors.toList());
         }
     }
        return eventShortDto;
    }

    private EventFullDto countViewsEventFullDtoId(EventFullDto eventFullDto, String uri, StatsClient client) {
        List<String> uris = new ArrayList<>(Collections.singleton(uri));
        ResponseEntity<Object> responseEntity = client.getStatsEvent(null, null, uris, true);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Object objects = responseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            assert objects != null;
            List<EventDtoResponse> listEvent = mapper.convertValue(objects, new TypeReference<>(){});
            if (listEvent.size() > 0) {
            eventFullDto.setViews(listEvent.stream().findFirst().get().getHits());
            } else {
                eventFullDto.setViews(0L);
           }
       }
        return eventFullDto;
    }

    private void saveStatistics(HttpServletRequest request, StatsClient client) {
        EventDto eventDto = EventDto.builder().ip(request.getRemoteAddr())
                .uri(request.getRequestURI()).timestamp(LocalDateTime.now().withNano(0).format(dateTimeFormatter))
                .app("exp-with-me-main-server").build();
        client.createEvent(eventDto);
    }

    private List<EventFullDto> countConfirmedRequests(List<EventFullDto> eventFullDto) {
        List<ParticipationRequest> requestsEvents = requestRepositoryJpa.findByStatusAndEventIdIn(StatusEnumRequest.CONFIRMED,
                eventFullDto.stream().map(EventFullDto::getId).collect(Collectors.toSet()));
        eventFullDto.forEach(eventShort -> eventShort.setConfirmedRequests(requestsEvents.stream()
                .filter(count -> eventShort.getId().equals(count.getEvent().getId())).count()));
        return eventFullDto;
    }
}
