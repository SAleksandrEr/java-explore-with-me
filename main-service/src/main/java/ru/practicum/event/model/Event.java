package ru.practicum.event.model;

import lombok.Data;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;
import ru.practicum.event.dto.StateEventEnum;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "annotation")
    private String annotation;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cati_id")
    private Category category;

    @Column(name = "participant_limit")
    private Integer participantLimit; // default 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private StateEventEnum state; // default PENDING;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "event_date")
    private LocalDateTime eventDate; // в формате "yyyy-MM-dd HH:mm:ss"

    @Column(name = "created_on")
    private LocalDateTime createdOn; // в формате "yyyy-MM-dd HH:mm:ss"

    @Column(name = "published_on")
    private LocalDateTime publishedOn; // (в формате "yyyy-MM-dd HH:mm:ss")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "init_id")
    private User initiator;

    @Column(name = "lat")
    private Float lat;

    @Column(name = "lon")
    private Float lon;

    @Column(name = "req_moderation")
    private Boolean requestModeration; // default TRUE;
}
