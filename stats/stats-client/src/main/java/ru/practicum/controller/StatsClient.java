package ru.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.EventDto;

import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    private static final String API_HIT = "/hit";

    private static final String API_STATS = "/stats";

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createEvent(EventDto eventDto) {
        return post(API_HIT, eventDto);
    }

    public ResponseEntity<Object> getStatsEvent(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters;
        if (uris == null) {
            parameters = Map.of("start", start, "end", end, "unique", unique);
            return get(API_STATS + "?start={start}&end={end}&unique={unique}", null, parameters);
        } else {
            parameters = Map.of("start", start, "end", end, "uris", uris.toArray(), "unique", unique);
            return get(API_STATS + "?start={start}&end={end}&uris={uris}&unique={unique}", null, parameters);
        }
    }
}
