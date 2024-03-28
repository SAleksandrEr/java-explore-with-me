package ru.practicum.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.event.dto.StateEventEnum;
import ru.practicum.event.model.Event;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepositoryJpa extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Optional<Event> findByIdAndState(Long id, StateEventEnum state);

    Optional<Event> findByCategoryId(Long id);

    Set<Event> findByIdIn(Set<Long> iDs);

}
