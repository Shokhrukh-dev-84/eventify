package org.example.best.event.repository;

import jakarta.persistence.LockModeType;
import org.example.best.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT e FROM Event e WHERE e.dateTime >= :from AND e.dateTime <= :to")
    Page<Event> findEventsWithPagination(LocalDateTime from,
                                         LocalDateTime to,
                                         Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdWithLock(@Param("id") Long id);

    boolean existsByTitle(String title);
}
