package ru.practicum;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.Objects;

@Data
@SuperBuilder
@NoArgsConstructor(force = true)
public class EventDtoResponse implements Comparable<EventDtoResponse> {

    private String app;

    private String uri;

    private Long hits;

    @Override
    public int compareTo(EventDtoResponse eventDtoResponse) {
        if (Objects.equals(eventDtoResponse.getHits(), getHits())) {
            return 0;
        } else if (eventDtoResponse.getHits() > getHits()) {
            return 1;
        } else {
            return -1;
        }
    }
}
