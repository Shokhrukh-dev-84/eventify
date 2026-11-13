package org.example.best.booking.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.best.BaseTest;
import org.example.best.TestDataFactory;
import org.example.best.booking.dto.request.CreateBookingRequest;
import org.example.best.booking.dto.request.UpdateBookingRequest;
import org.example.best.booking.dto.response.BookingResponse;
import org.example.best.booking.mapper.BookingMapper;
import org.example.best.booking.model.Booking;
import org.example.best.booking.repository.BookingRepository;
import org.example.best.config.SecurityUtils;
import org.example.best.event.repository.EventRepository;
import org.example.best.exception.UserNotAuthenticatedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest extends BaseTest{

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BookingMapper bookingMapper;

    private MockedStatic<SecurityUtils> mockedSecurity;

    @InjectMocks
    private BookingService bookingService;

    private Long id;
    private Booking fakeBooking;
    private BookingResponse fakeBookingResponse;

    @BeforeEach
    void setUp() {
        mockedSecurity = Mockito.mockStatic(SecurityUtils.class);
        id = TestDataFactory.DEFAULT_ID;
        fakeBooking = TestDataFactory.defaultBooking();
        fakeBookingResponse = TestDataFactory.defaultBookingResponse();
    }

    @AfterEach
    void tearDown() {
        if (mockedSecurity != null) {
            mockedSecurity.close();
        }
    }

    @Test
    @DisplayName("Получить бронирование по ID")
    void bookingsIdGet_WhenBookingExistAndIdNotNull_ReturnBookingResponse() {

        when(bookingRepository.findById(id)).thenReturn(Optional.of(fakeBooking));

        when(bookingMapper.toResponse(fakeBooking)).thenReturn(fakeBookingResponse);

        BookingResponse response = bookingService.bookingsIdGet(id);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getTicketCount()).isEqualTo(50);
        assertThat(response).returns(1L, BookingResponse::getId);
    }

    @Test
    @DisplayName("Если по переданному ID бронирование не существует -> выброс исключение")
    void bookingsIdGet_WhenBookingDoesNotExistForThisId_ThrowNotFoundException() {

        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.bookingsIdGet(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Бронирование с ID " + id + " не найдено!");
        verifyNoInteractions(bookingMapper);
    }

    @Test
    @DisplayName("Получить список бронирований")
    void bookingsGet_WhenBookingExist_ReturnBookingResponsesList() {
      String currentUserName = TestDataFactory.DEFAULT_EMAIL;
        mockedSecurity.when(SecurityUtils::getUserName).thenReturn(currentUserName);

      when(bookingRepository.findAllByUserEmail(currentUserName)).thenReturn(List.of(fakeBooking));
      when(bookingMapper.toResponse(fakeBooking)).thenReturn(fakeBookingResponse);

        List<BookingResponse> responseList = bookingService.bookingsGet();

        assertThat(responseList)
                .isNotNull()
                .isNotEmpty()
                .containsExactly(fakeBookingResponse);
    }

    @Test
    @DisplayName("Если текущего пользователя не существует -> выброс исключение")
    void bookingsGet_WhenCurrentUserIsNull_ReturnThrowException() {
        mockedSecurity.when(SecurityUtils::getUserName).thenReturn(null);

        assertThatThrownBy(() -> bookingService.bookingsGet())
                .isInstanceOf(UserNotAuthenticatedException.class)
                .hasMessageContaining("Пользователь не авторизован");
        verifyNoInteractions(bookingMapper);
    }

    @Test
    @DisplayName("Обновление существуешего бронирования")
    void bookingsIdPut_WhenBookingExistAndIdNotNull_UpdatedAndReturnBookingResponse() {
        var fakeRequest = TestDataFactory.defaultUpdateBookingRequest();

        when(bookingRepository.findById(id)).thenReturn(Optional.of(fakeBooking));

        when(bookingRepository.save(any(Booking.class))).thenReturn(fakeBooking);
        when(bookingMapper.toResponse(fakeBooking)).thenReturn(fakeBookingResponse);

        BookingResponse updated = bookingService.bookingsIdPut(id, fakeRequest);

        assertThat(updated).isNotNull();
        assertThat(updated.getTicketCount()).isEqualTo(fakeRequest.getTicketCount());
    }

    @Test
    @DisplayName("Если по переданному ID бронирование не существует -> выброс исключение")
    void bookingsIdPut_WhenBookingDoesNotExist_ThrowException() {
        var fakeRequest = TestDataFactory.defaultUpdateBookingRequest();

        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->bookingService.bookingsIdPut(id, fakeRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Бронирование с ID " + id + " не найдено!");
        verifyNoInteractions(bookingMapper);
    }

    @Test
    @DisplayName("Если по переданному ID бронирование существует -> удаление")
    void bookingsIdDelete_WhenBookingExistById_Delete() {
        when(bookingRepository.findById(id)).thenReturn(Optional.of(fakeBooking));

        bookingService.bookingsIdDelete(id);

        verify(bookingRepository).delete(fakeBooking);

    }
    @Test
    @DisplayName("Если запрос валидный -> создается бронирование")
    void bookingsPost_WhenRequestIsValid_ReturnsBookingResponse() {
        String currentUser = TestDataFactory.DEFAULT_EMAIL;
        mockedSecurity.when(SecurityUtils::getUserName).thenReturn(currentUser);

        var fakeEvent = TestDataFactory.defaultEvent();
        var fakeRequest = TestDataFactory.defaultCreateBookingRequest();
        when(eventRepository.findById(id)).thenReturn(Optional.of(fakeEvent));
        when(bookingMapper.toResponse(any(Booking.class))).thenReturn(fakeBookingResponse);
        when(bookingRepository.save(any(Booking.class))).thenReturn(fakeBooking);

        BookingResponse response = bookingService.bookingsPost(fakeRequest);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
    }
}