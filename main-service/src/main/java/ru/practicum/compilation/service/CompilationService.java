package ru.practicum.compilation.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.QCompilation;
import ru.practicum.compilation.storage.CompilationRepositoryJpa;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.storage.EventRepositoryJpa;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.requests.dto.StatusEnumRequest;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.storage.RequestRepositoryJpa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class CompilationService {

    private final CompilationMapper compilationMapper;

    private final CompilationRepositoryJpa compilationRepositoryJpa;

    private final EventRepositoryJpa eventRepositoryJpa;

    private final EventMapper eventMapper;

    private final RequestRepositoryJpa requestRepositoryJpa;

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto compDtoNew) {
        Compilation compilation = compilationMapper.toCompilation(compDtoNew);
        Set<Event> listEventComp = new HashSet<>();
        if (compDtoNew.getEvents() != null) {
            listEventComp = eventRepositoryJpa.findByIdIn(compDtoNew.getEvents());
        }
        compilation.setEvents(listEventComp);
        compilationRepositoryJpa.save(compilation);
        if (compDtoNew.getEvents() != null) {
            return mappingToCompilationDto(compilation);
        } else {
            return compilationMapper.toCompilationDto(compilation);
        }
    }

    @Transactional
    public CompilationDto updateCompilation(UpdateCompilationRequest compUpdate, Long compId) {
        Compilation compilation = compilationRepositoryJpa.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found", "The required object was not found."));
        if (compUpdate.getPinned() != null) {
            compilation.setPinned(compUpdate.getPinned());
        }
        if (compUpdate.getTitle() != null) {
            compilation.setTitle(compUpdate.getTitle());
        }
        if (compUpdate.getEvents() != null) {
            Set<Event> listEventComp = eventRepositoryJpa.findByIdIn(compUpdate.getEvents());
            compilation.setEvents(listEventComp);
            compilationRepositoryJpa.save(compilation);
            return mappingToCompilationDto(compilation);
        } else {
            compilationRepositoryJpa.save(compilation);
            return compilationMapper.toCompilationDto(compilation);
        }
    }

    @Transactional
    public void compilationDeleteById(Long compId) {
        compilationRepositoryJpa.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found", "The required object was not found."));
        compilationRepositoryJpa.deleteById(compId);
    }

    public List<CompilationDto> findCompilation(Pageable page) {
        QCompilation compilation = QCompilation.compilation;
        BooleanExpression condition = compilation.isNotNull();
        Sort sort = Sort.by("id");
        PageRequest pageRequest = PageRequest.of(page.getPageNumber(), page.getPageSize(), sort);
        List<Compilation> compilationList = compilationRepositoryJpa.findAll(condition, pageRequest)
                .stream().collect(Collectors.toList());
        return compilationList.stream().map(this::mappingToCompilationDto).collect(Collectors.toList());
    }

    public CompilationDto findCompilationId(Long compId) {
        Compilation compilation = compilationRepositoryJpa.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found", "The required object was not found."));
        return mappingToCompilationDto(compilation);
    }

    private CompilationDto mappingToCompilationDto(Compilation compilation) {
        List<ParticipationRequest> requestsEvents = requestRepositoryJpa.findByStatusAndEventIdIn(StatusEnumRequest.CONFIRMED,
                compilation.getEvents().stream().map(Event::getId).collect(Collectors.toSet()));
        Set<EventShortDto> eventShortDto = compilation.getEvents().stream().map(eventMapper::toEventShortDto).collect(Collectors.toSet());
        eventShortDto.forEach(eventShort -> eventShort.setConfirmedRequests(requestsEvents.stream()
                .filter(count -> eventShort.getId().equals(count.getEvent().getId())).count()));
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        compilationDto.setEvents(eventShortDto);
        return compilationDto;
    }
}
