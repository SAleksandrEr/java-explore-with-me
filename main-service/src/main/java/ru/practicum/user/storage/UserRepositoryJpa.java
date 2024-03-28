package ru.practicum.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.user.model.User;

@Repository
public interface UserRepositoryJpa extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

}
