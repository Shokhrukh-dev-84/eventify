package org.example.best.booking.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.best.BaseTest;
import org.example.best.TestDataFactory;
import org.example.best.auth.jwt.JwtCore;
import org.example.best.booking.dto.request.CreateBookingRequest;
import org.example.best.booking.dto.request.UpdateBookingRequest;
import org.example.best.booking.dto.response.BookingResponse;
import org.example.best.booking.service.BookingService;
import org.example.best.common.dto.pagination.PageableBookingResponse;
import org.example.best.exception.UserNotAuthenticatedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(value = BookingController.class, excludeAutoConfiguration = {
SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@Import(SpringDataWebConfiguration.class)
class BookingControllerTest extends BaseTest{
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private BookingService bookingService;
    @MockitoBean
    private JwtCore jwtCore;
    @MockitoBean
    private UserDetailsService userDetailsService;


    @Test
    @DisplayName("Успешное получение бронирование по ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void bookingsIdGet_WhenBookingExistById_Return200AndJson() throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        BookingResponse response = TestDataFactory.defaultBookingResponse();

        when(bookingService.bookingsIdGet(id))
                .thenReturn(response);
        mockMvc.perform(get("/bookings/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.ticketCount").value(response.getTicketCount()));
    }

    @ParameterizedTest(name = "Полчить бронирование по ID -> ошибка {1}")
    @MethodSource("provideExceptionForGetBookingsById")
    @WithMockUser(username = "user", roles = "{USER}")
    void bookingsIdGet_WhenExceptionThrown_ThrowsExpectedStatus(RuntimeException exception,
                                                                int expected) throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        when(bookingService.bookingsIdGet(id)).thenThrow(exception);

        assertGetRequestThrows("/bookings/{id}", id, expected);
    }

    private static Stream<Arguments> provideExceptionForGetBookingsById() {
        return Stream.of(
                Arguments.of(new UserNotAuthenticatedException("Не авторизован"), 401),
                Arguments.of(new AccessDeniedException("Доступ запрещён"), 403),
                Arguments.of(new EntityNotFoundException("Бронирование не найдено"), 404)
        );
    }

    @Test
    @DisplayName("Обновление бронирование по ID")
    @WithMockUser(username = "email", roles = {"USER"})
    void bookingsIdPut_WhenBookingExistById_ReturnOkUpdated() throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        BookingResponse response = TestDataFactory.defaultBookingResponse();
        UpdateBookingRequest request = TestDataFactory.defaultUpdateBookingRequest();
        var json = toJson(request);

        when(bookingService.bookingsIdPut(eq(id), any(UpdateBookingRequest.class))).thenReturn(response);
        mockMvc.perform(put("/bookings/{id}", id).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.ticketCount").value(response.getTicketCount()));
    }

    @ParameterizedTest(name = "Обновление бронирование -> ошибка {1}")
    @MethodSource("provideExceptionForPutBooking")
    @WithMockUser(username = "user", roles = "{USER}")
    void bookingsIdPut_WhenExceptionThrown_ThrowsExpectedStatus(RuntimeException exception,
                                                                int expected) throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        var request = TestDataFactory.defaultUpdateBookingRequest();
        when(bookingService.bookingsIdPut(anyLong(), any(UpdateBookingRequest.class)))
                .thenThrow(exception);
        assertPutRequestThrows("/bookings/{id}", id, request, expected);
    }

    private static Stream<Arguments> provideExceptionForPutBooking() {
        return Stream.of(
        Arguments.of(new UserNotAuthenticatedException("Не авторизован"), 401),
                Arguments.of(new AccessDeniedException("Доступ запрещён"), 403),
                Arguments.of(new EntityNotFoundException("Бронирование не найдено"), 404),
                Arguments.of(new IllegalArgumentException("Неверный запрос"), 400)
        );
    }

    @Test
    @DisplayName("Удаление бронирование по ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void bookingsIdDelete_WhenBookingExistById_Return204() throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        doNothing().when(bookingService).bookingsIdDelete(id);
        mockMvc.perform(delete("/bookings/{id}", id).with(csrf()))
                .andExpect(status().isNoContent());
        verify(bookingService).bookingsIdDelete(id);
    }

    @ParameterizedTest(name = "Создать бронирование -> ошибка {1}")
    @MethodSource("provideExceptionForDeleteBookingById")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void bookingsIdDelete_WhenExceptionThrown_ThrowsExpectedStatus(RuntimeException exception, int expected)
            throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        doThrow(exception).when(bookingService).bookingsIdDelete(id);

        assertDeleteRequestThrows("/bookings/{id}", id, expected);
    }

    private static Stream<Arguments> provideExceptionForDeleteBookingById() {
        return Stream.of(
                Arguments.of(new IllegalArgumentException("Ошибка валидации"), 400),
                Arguments.of(new UserNotAuthenticatedException("Не авторизован"), 401),
                Arguments.of(new AccessDeniedException("Доступ запрещён"), 403),
                Arguments.of(new EntityNotFoundException("Мероприятие не найдено"), 404)
        );
    }

    @Test
    @DisplayName("Получить список бронирований")
    @WithMockUser(username = "email", roles = {"USER"})
    void bookingsGet_WhenBookingExist_Return200() throws Exception {
        var fakeBookingResponse = TestDataFactory.defaultBookingResponse();

        when(bookingService.bookingsGet()).thenReturn(List.of(fakeBookingResponse));
        mockMvc.perform(get("/bookings").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(fakeBookingResponse.getId()));
    }

    @Test
    @DisplayName("Получить свои бронирования -> ошибка 401 ")
    @WithMockUser(username = "email", roles = {"USER"})
    void bookingsGet_WhenNotAuthorized_Throw401() throws Exception {
        when(bookingService.bookingsGet()).thenThrow(new UserNotAuthenticatedException("Не авторизован"));
        mockMvc.perform(get("/bookings").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Создание мероприятия")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void bookingsPost_WhenRequestIsValid_Return201() throws Exception {
        BookingResponse fakeBookingResponse = TestDataFactory.defaultBookingResponse();
        CreateBookingRequest request = TestDataFactory.defaultCreateBookingRequest();

        when(bookingService.bookingsPost(any(CreateBookingRequest.class))).thenReturn(fakeBookingResponse);
        mockMvc.perform(post("/bookings").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(fakeBookingResponse.getId()))
                .andExpect(jsonPath("$.ticketCount").value(fakeBookingResponse.getTicketCount()));
    }

    @ParameterizedTest(name = "Создать бронирование -> ошибка {1}")
    @MethodSource("provideExceptionForCreateBooking")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void bookingsPost_WhenExceptionThrown_ThrowsExpectedStatus(RuntimeException exception, int expected) throws Exception {
        var fakeRequest = TestDataFactory.defaultCreateBookingRequest();
        when(bookingService.bookingsPost(any(CreateBookingRequest.class))).thenThrow(exception);

        assertPostRequestThrows("/bookings", fakeRequest, expected);
    }

    private static Stream<Arguments> provideExceptionForCreateBooking() {
        return Stream.of(
                Arguments.of(new IllegalArgumentException("Ошибка валидации"), 400),
                Arguments.of(new UserNotAuthenticatedException("Не авторизован"), 401),
                Arguments.of(new AccessDeniedException("Доступ запрещён"), 403),
                Arguments.of(new EntityNotFoundException("Мероприятие не найдено"), 404)
        );
    }


    @Test
    @DisplayName("Подтверждение бронирование по ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminBookingsIdConfirm_WhenBookingExistById_Return204() throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        doNothing().when(bookingService).adminBookingsIdConfirm(id);
        mockMvc.perform(put("/admin/bookings/{id}/confirm", id).with(csrf()))
                .andExpect(status().isNoContent());
        verify(bookingService).adminBookingsIdConfirm(id);
    }

    @ParameterizedTest(name = "Подтвердить бронирование (Admin) -> ошибка {1}")
    @MethodSource("provideExceptionForAdminConfirmed")
    @WithMockUser(username = "admin", roles = "{ADMIN}")
    void adminBookingsIdConfirmed_WhenExceptionThrown_ThrowsExpectedStatus(RuntimeException exception,
                                                                           int expected) throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        var fakeRequest = TestDataFactory.defaultCreateBookingRequest();
        doThrow(exception).when(bookingService).adminBookingsIdConfirm(anyLong());
       mockMvc.perform(put("/admin/bookings/{id}/confirm", id).with(csrf())
               .contentType(MediaType.APPLICATION_JSON)
               .content(toJson(fakeRequest)))
               .andExpect(status().is(expected));
    }

    private static Stream<Arguments> provideExceptionForAdminConfirmed() {
        return Stream.of(
            Arguments.of(new UserNotAuthenticatedException("Не авторизован"), 401),
            Arguments.of(new AccessDeniedException("Доступ запрещён"), 403),
            Arguments.of(new EntityNotFoundException("Бронирование не найдено"), 404)
        );
    }

    @Test
    @DisplayName("Получить список бронирований с пагинацией")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminBookingsGet_WhenRequestIsValid_Return200AndJsonArray() throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        Boolean unconfirmedOnly = true;
        PageableBookingResponse fakeResponse = new PageableBookingResponse();
         when(bookingService.adminBookingsGet(anyLong(), anyBoolean(), any(Pageable.class)))
                 .thenReturn(fakeResponse);
         mockMvc.perform(get("/admin/bookings").with(csrf())
                 .param("id", id.toString())
                 .param("unconfirmedOnly", unconfirmedOnly.toString())
                 .param("page", "0")
                 .param("size", "10"))
                 .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Получить список бронирований с пагинацией -> ошибка 401")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminBookingsGet_WhenNotAuthorized_Throw401() throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        Boolean unconfirmedOnly = true;
        when(bookingService.adminBookingsGet(any(), any(), any(Pageable.class)))
                .thenThrow(new UserNotAuthenticatedException("Не авторизован"));
        mockMvc.perform(get("/admin/bookings")
                .param("id", id.toString())
                .param("unconfirmedOnly", unconfirmedOnly.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Получить список бронирований с пагинацией -> ошибка 403")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminBookingsGet_WhenNoAccess_Throw403() throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        Boolean unconfirmedOnly = true;
        when(bookingService.adminBookingsGet(any(), any(), any(Pageable.class)))
                .thenThrow(new AccessDeniedException("Доступ запрещён"));
        mockMvc.perform(get("/admin/bookings")
                        .param("id", id.toString())
                        .param("unconfirmedOnly", unconfirmedOnly.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Удаление бронирование по ID с ролью админа")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminBookingsIdDelete_WhenBookingExistById_Return204() throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        doNothing().when(bookingService).adminBookingsIdDelete(id);
        mockMvc.perform(delete("/admin/bookings/{id}", id).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest(name = "Удалить бронирование по ID (Admin)")
    @MethodSource("provideExceptionForAdminBookingsDeleteById")
    @WithMockUser(username = "admin", roles = "{ADMIN}")
    void adminBookingsIdDelete_WhenExceptionThrown_ThrowsExpectedStatus(RuntimeException exception,
                                                                        int expected) throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        doThrow(exception).when(bookingService).adminBookingsIdDelete(id);
        assertDeleteRequestThrows("/admin/bookings/{id}", id, expected);

    }

    private static Stream<Arguments> provideExceptionForAdminBookingsDeleteById() {
        return Stream.of(
                Arguments.of(new UserNotAuthenticatedException("Не авторизован"), 401),
                Arguments.of(new AccessDeniedException("Доступ запрещён"), 403),
                Arguments.of(new EntityNotFoundException("Бронирование не найдено"), 404)
        );
    }
}