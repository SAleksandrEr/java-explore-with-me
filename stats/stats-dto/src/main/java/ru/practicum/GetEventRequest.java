package ru.practicum;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
public class GetEventRequest {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime start;
    LocalDateTime end;
    List<String> uris;
    Boolean unique;

    public static GetEventRequest of(String start, String end, List<String> uris, Boolean unique) {
        GetEventRequest request = new GetEventRequest();
        request.setStart(LocalDateTime.parse(start, dateTimeFormatter));
        request.setEnd(LocalDateTime.parse(end, dateTimeFormatter));
        request.setUnique(unique);
        if (uris != null) {
            request.setUris(uris);
        }
        return request;
    }

    public boolean hasUris() {
        return uris != null && uris.isEmpty();
    }
}
