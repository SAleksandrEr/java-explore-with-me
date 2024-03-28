package ru.practicum.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GetUserRequest {

    List<Long> users;

    int from;

    int size;

    public static GetUserRequest of(List<Long> users, int from, int size) {
        GetUserRequest request = new GetUserRequest();
        request.setFrom(from);
        request.setSize(size);
        if (users != null) {
            request.setUsers(users);
        }
        return request;
    }

    public boolean hasUsers() {
        return users != null && !users.isEmpty();
    }
}
