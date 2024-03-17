package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;

@Repository
public interface EventRepositoryJpa extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
}
