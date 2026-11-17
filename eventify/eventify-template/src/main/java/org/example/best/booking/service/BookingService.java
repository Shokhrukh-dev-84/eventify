package org.example.best.booking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.best.booking.dto.request.CreateBookingRequest;
import org.example.best.booking.dto.request.UpdateBookingRequest;
import org.example.best.booking.dto.response.BookingResponse;
import org.example.best.booking.mapper.BookingMapper;
import org.example.best.booking.model.Booking;
import org.example.best.booking.repository.BookingRepository;
import org.example.best.common.dto.pagination.PageableBookingResponse;
import org.example.best.config.SecurityUtils;
import org.example.best.event.model.Event;
import org.example.best.event.repository.EventRepository;
import org.example.best.exception.UserNotAuthenticatedException;
import org.example.best.user_profile.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final BookingMapper mapper;

    @Transactional(readOnly = true)
    @PreAuthorize("@resourceSecurity.isBookingOwner(#id)")
    @Cacheable(value = "bookings", key = "#id")
    public BookingResponse bookingsIdGet(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Бронирование с ID " + id + " не найдено!"));

        return mapper.toResponse(booking);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    @Cacheable(value = "bookingsList", key = "T(org.example.best.config.SecurityUtils).getUserName()")
    public List<BookingResponse> bookingsGet() {
        String currentUserName = SecurityUtils.getUserName();
        if(currentUserName == null || currentUserName.isBlank()) {
            throw new UserNotAuthenticatedException("Пользователь не авторизован");
        }
        List<Booking> bookings = bookingRepository.findAllByUserEmail(currentUserName);
        if(bookings == null) {
            return Collections.emptyList();
        }
       return bookings.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("@resourceSecurity.isBookingOwner(#id)")
    @Caching(
            evict = {
                    @CacheEvict(value = "bookings", key = "#id"),
                    @CacheEvict(value = "bookingsList", allEntries = true)
            }
    )
    public BookingResponse bookingsIdPut(Long id, UpdateBookingRequest request) {
        Booking currentBooking = bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Бронирование с ID " + id + " не найдено!"));

        Event event = currentBooking.getEvent();
        Integer oldTicketCount = currentBooking.getTicketCount();
        Integer newTicketCount = request.getTicketCount();
        Integer difference = newTicketCount - oldTicketCount;

        if(difference > event.getAvailableTickets()) {
            throw new IllegalArgumentException("Недостаточно доступных билетов на мероприятия");
        }
        event.setAvailableTickets(event.getAvailableTickets()- difference);

        currentBooking.setTicketCount(newTicketCount);
        Booking updateBooking = bookingRepository.save(currentBooking);

        log.info("Бронирование с ID: {} на мероприятие: {} успешно обновлён. Актуальное количество билетов: {}",
                id, event.getTitle(), newTicketCount);
        return mapper.toResponse(updateBooking);
    }

    @Transactional
    @PreAuthorize("@resourceSecurity.isBookingOwner(#id)")
    @Caching(
            evict = {
                    @CacheEvict(value = "bookings", key = "#id"),
                    @CacheEvict(value = "bookingsList", allEntries = true)
            }
    )
    public void bookingsIdDelete(Long id) {
        Booking currentBooking = bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Бронирование с ID " + id + " не найдено!"));
        Integer returnTicketsCount = currentBooking.getTicketCount();
        Event event = currentBooking.getEvent();
        event.setAvailableTickets(event.getAvailableTickets() + returnTicketsCount);

        eventRepository.save(event);
        log.info("Пользователь {} удалил своё бронирование с ID: {}. Возвращено билетов: {} на мероприятие: {}",
                currentBooking.getUser().getUsername(),
                currentBooking.getId(),
                returnTicketsCount,
                event.getTitle());
        bookingRepository.delete(currentBooking);

    }

    @Transactional
    @CacheEvict(value = "bookingsList", allEntries = true)
    public BookingResponse bookingsPost(CreateBookingRequest request) {
        User currentUser = SecurityUtils.getCurrentUser().orElseThrow(
                () -> new UserNotAuthenticatedException("Пользователь не найден")
        );
        Event event = eventRepository.findByIdWithLock(request.getEventId()).orElseThrow(() ->
                new EntityNotFoundException("Мероприятие не найдено!"));
        if(event.getAvailableTickets() < request.getTicketCount()) {
            throw new IllegalArgumentException("Недостаточно билетов");
        }
        event.setAvailableTickets(event.getAvailableTickets() - request.getTicketCount());
        Booking booking = new Booking();
        booking.setConfirmed(false);
        booking.setTicketCount(request.getTicketCount());
        booking.setEvent(event);
        booking.setUser(currentUser);
        booking.setTimezone("Europe/Moscow");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setExpiryTime(LocalDateTime.now().plusMinutes(15));
        Booking savedBooking = bookingRepository.save(booking);

        log.info("Пользователь {} успешно забронировал {} билет(ов) на мероприятие: {}",
                currentUser.getEmail(),
                request.getTicketCount(),
                savedBooking.getEvent().getTitle());
        return mapper.toResponse(savedBooking);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void adminBookingsIdConfirm(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Бронирование с ID " + id + " не найдено!"));
        if(booking.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Время действия брони истекло");
        }
        booking.setConfirmed(true);
        bookingRepository.save(booking);
        log.info("Бронь с ID: {} подтверждён", id);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PageableBookingResponse adminBookingsGet(Long eventId, Boolean unconfirmedOnly, Pageable pageable) {
        Boolean confirmed = (unconfirmedOnly != null) ? !unconfirmedOnly : null;
        Page<Booking> bookings = bookingRepository.findAllBookings(eventId, confirmed,
                pageable);
        return mapper.toPageableResponse(bookings, pageable);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Caching(
            evict = {
                    @CacheEvict(value = "bookings", key = "#id"),
                    @CacheEvict(value = "bookingsList", allEntries = true)
            }
    )
    public void adminBookingsIdDelete(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Бронирование по ID: " + id + " не найдено")
        );
        if(booking.getConfirmed()) {
            throw new IllegalStateException("Нельзя удалит бронирование со статусом 'подтверждён'!");
        }

        Event event = booking.getEvent();
        int updateAvailableTickets = event.getAvailableTickets() + booking.getTicketCount();
        event.setAvailableTickets(updateAvailableTickets);
        eventRepository.save(event);
        bookingRepository.delete(booking);
        log.info("Администратор успешно удалил бронирование с ID: {}", id);
    }
}
