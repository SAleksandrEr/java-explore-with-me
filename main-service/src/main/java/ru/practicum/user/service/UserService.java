package ru.practicum.user.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.user.dto.GetUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.QUser;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepositoryJpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class UserService {

    private final UserMapper userMapper;

    private final UserRepositoryJpa userRepositoryJpa;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.touserDto(userRepositoryJpa.save(user));
    }

    @Transactional
    public void deleteUserById(Long id) {
        userRepositoryJpa.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + id + " was not found", "The required object was not found."));
        userRepositoryJpa.deleteById(id);
    }

    public List<UserDto> findUsersIds(GetUserRequest request) {
        QUser user = QUser.user;
        List<BooleanExpression> conditions = new ArrayList<>();
        if (request.hasUsers()) {
            conditions.add(user.id.in(request.getUsers()));
        } else {
            conditions.add(user.isNotNull());
        }
        Optional<BooleanExpression> finalCondition = conditions.stream()
                .reduce(BooleanExpression::and);
        Sort sort = Sort.by("id");
        PageRequest pageRequest = PageRequest.of(request.getFrom(), request.getSize(), sort);
        List<User> userList = userRepositoryJpa.findAll(Objects.requireNonNull(finalCondition.get()), pageRequest)
                .stream().collect(Collectors.toList());
        return userList.stream().map(userMapper::touserDto).collect(Collectors.toList());
    }

}
