package ru.practicum.requests.model;

import lombok.Data;
import ru.practicum.user.model.User;
import ru.practicum.event.model.Event;
import ru.practicum.requests.dto.StatusEnumRequest;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEnumRequest status;

    @Column(name = "created")
    private LocalDateTime created;
}
