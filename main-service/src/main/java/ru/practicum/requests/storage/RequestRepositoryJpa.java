package ru.practicum.requests.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.requests.dto.StatusEnumRequest;
import ru.practicum.requests.model.ParticipationRequest;

import java.util.List;
import java.util.Set;

@Repository
public interface RequestRepositoryJpa extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findByEventIdAndStatus(Long eventId, StatusEnumRequest status);

    @Modifying
    @Query("UPDATE ParticipationRequest " +
            "SET status = ?1 " +
            "where id = ?2")
    void updateRequest(StatusEnumRequest status, Long requestId);

    List<ParticipationRequest> findByRequesterId(Long userId);

    List<ParticipationRequest> findByEventIdAndIdIn(Long eventId, List<Long> RequestIds);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByStatusAndEventIdIn(StatusEnumRequest status, Set<Long> eventId);
}
