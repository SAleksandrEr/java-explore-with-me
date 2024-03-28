package ru.practicum.category.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@NoArgsConstructor
public class GetCategoriesRequest {

    int from;

    int size;

    public static Pageable pageRequest(Integer from, Integer size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }
}
