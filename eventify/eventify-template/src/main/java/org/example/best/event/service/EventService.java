package org.example.best.event.service;

import lombok.extern.slf4j.Slf4j;
import org.example.best.exception.UserNotAuthenticatedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.best.common.dto.pagination.PageableEventResponse;
import org.example.best.config.SecurityUtils;
import org.example.best.event.dto.request.EventCreateRequest;
import org.example.best.event.dto.request.EventUpdateRequest;
import org.example.best.event.dto.response.EventResponse;
import org.example.best.event.mapper.EventMapper;
import org.example.best.event.model.Event;
import org.example.best.event.repository.EventRepository;
import org.example.best.user_profile.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "events", key = "#id")
    public EventResponse eventsIdGet(Long id) {
        Event event = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Мероприятие с ID: " + id + " не найдено!"));
        return eventMapper.toResponse(event);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Caching(
            evict = {
                    @CacheEvict(value = "events", key = "#id"),
                    @CacheEvict(value = "eventsList", allEntries = true)
            }
    )
    public EventResponse eventsIdPut(Long id, EventUpdateRequest request) {
        Event currentEvent = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Мероприятие с ID: " + id + " не найдено!"));

        int soldTickets = currentEvent.getTotalTickets() - currentEvent.getAvailableTickets();
        if (request.getTotalTickets() < soldTickets) {
            throw new IllegalArgumentException("Обновлямое количество билетов не может быть " +
                    "меньше забронированных");
        }

        eventMapper.updateEntityFromDto(request, currentEvent);

        currentEvent.setAvailableTickets(currentEvent.getTotalTickets() - soldTickets);
        Event updateEvent = repository.save(currentEvent);
        log.info("Бронирование с ID: {} на мероприятие: {} обновлено", id, updateEvent.getTitle());
        return eventMapper.toResponse(updateEvent);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Caching(
            evict = {
                    @CacheEvict(value = "events", key = "#id"),
                    @CacheEvict(value = "eventsList", allEntries = true)
            }
    )
    public void eventsIdDelete(Long id) {
        Event event = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Мероприятие с ID: " + id + " не найдено!"));

        if (!event.getAvailableTickets().equals(event.getTotalTickets())) {
            throw new IllegalArgumentException("Нельзя удалить меорприятие на котрое имеются " +
                    "забронированные билеты!");
        }
        repository.delete(event);
        log.info("Мероприятие с ID: {} успешно удалено", id);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "eventsList", allEntries = true)
    public EventResponse eventsPost(EventCreateRequest request) {
        if (repository.existsByTitle(request.getTitle())) {
            throw new IllegalArgumentException("Мероприятие с таким названием уже существует");
        }
        Event event = eventMapper.toEntity(request);
        event.setAvailableTickets(request.getTotalTickets());

        User creator = SecurityUtils.getCurrentUser().orElseThrow(
                () -> new UserNotAuthenticatedException("Пользователь не авторизован")
        );
        event.setCreator(creator);

        Event saved = repository.save(event);
        log.info("Мероприятие '{}' успешно создано администратором {}", saved.getTitle(), creator.getEmail());
        return eventMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "eventsList")
    public PageableEventResponse eventsGet(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        LocalDateTime actualFrom = (from != null) ? from :
                LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime actualTo = (to != null) ? to :
                LocalDateTime.of(2099, 12, 31, 23, 59);
        Page<Event> events = repository.findEventsWithPagination(actualFrom, actualTo, pageable);
        return eventMapper.toPageableResponse(events);
    }
}
