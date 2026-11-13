package org.example.best.event.repository;

import org.example.best.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT e FROM Event e WHERE e.dateTime >= :from AND e.dateTime <= :to")
    Page<Event> findEventsWithPagination(LocalDateTime from,
                                         LocalDateTime to,
                                         Pageable pageable);
    boolean existsByTitle(String title);
}
