package org.example.best.event.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.best.BaseTest;
import org.example.best.TestDataFactory;
import org.example.best.auth.jwt.JwtCore;
import org.example.best.common.dto.pagination.PageableEventResponse;
import org.example.best.common.mapper.PageableMapper;
import org.example.best.event.dto.request.EventCreateRequest;
import org.example.best.event.dto.request.EventUpdateRequest;
import org.example.best.event.service.EventService;
import org.example.best.exception.UserNotAuthenticatedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
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

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = EventController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@Import(SpringDataWebConfiguration.class)
class EventControllerTest extends BaseTest {

    @MockitoBean
    private EventService eventService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PageableMapper pageableMapper;

    @MockitoBean
    private JwtCore jwtCore;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Создать мероприятие (Admin)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void eventsPost_WhenRequestIsValid_ReturnEventResponse() throws Exception {
        var fakeEventRequest = TestDataFactory.defaultEventCreateRequest();
        var fakeEventResponse = TestDataFactory.defaultEventResponse();
        when(eventService.eventsPost(any(EventCreateRequest.class))).thenReturn(fakeEventResponse);
        mockMvc.perform(post("/admin/events").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(fakeEventRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(fakeEventResponse.getId()))
                .andExpect(jsonPath("$.totalTickets").value(fakeEventResponse.getTotalTickets()));
    }

    @ParameterizedTest(name = "Создать мероприятие -> ошибка {1}")
    @MethodSource("provideExceptionForCreateEvent")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void eventsPost_WhenExceptionThrown_ThrowsExpectedStatus(RuntimeException exception, int expected) throws Exception {
        var fakeEventRequest = TestDataFactory.defaultEventCreateRequest();
        when(eventService.eventsPost(any(EventCreateRequest.class))).thenThrow(exception);

        assertPostRequestThrows("/admin/events", fakeEventRequest, expected);
    }

    private static Stream<Arguments> provideExceptionForCreateEvent() {
        return Stream.of(
                Arguments.of(new IllegalArgumentException("Неверный запрос"), 400),
                Arguments.of(new UserNotAuthenticatedException("Не авторизован"), 401),
                Arguments.of(new AccessDeniedException("Доступ запрещён"), 403),
                Arguments.of(new IllegalStateException("Конфликт"), 409)
        );
    }

    @Test
    @DisplayName("Обновить мероприятие по ID (Admin)")
    @WithMockUser(username = "admin", roles = "{ADMIN}")
    void eventsIdPut_WhenRequestIsValid_Return200AndEventResponse() throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        var fakeUpdateRequest = TestDataFactory.defaultEventUpdateRequest();
        var fakeEventResponse = TestDataFactory.defaultEventResponse();

        when(eventService.eventsIdPut(anyLong(), any(EventUpdateRequest.class)))
                .thenReturn(fakeEventResponse);
        mockMvc.perform(put("/admin/events/{id}", id).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(fakeUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fakeEventResponse.getId()));

    }

    @ParameterizedTest(name = "Обновление бронирования по ID (Admin) -> ошибка {1}")
    @MethodSource("provideExceptionForEventsIdPut")
    @WithMockUser(username = "admin", roles = "{ADMIN}")
    void eventsIdPut_WhenExceptionThrown_ThrowsExpectedStatus(RuntimeException exception,
                                                              int expected) throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        var fakeEventUpdateRequest = TestDataFactory.defaultEventUpdateRequest();
        when(eventService.eventsIdPut(anyLong(), any(EventUpdateRequest.class)))
                .thenThrow(exception);
        assertPutRequestThrows("/admin/events/{id}", id, fakeEventUpdateRequest, expected);
    }

    private static Stream<Arguments> provideExceptionForEventsIdPut() {
        return Stream.of(
                Arguments.of(new IllegalArgumentException("Неверный запрос"), 400),
                Arguments.of(new UserNotAuthenticatedException("Не авторизован"), 401),
                Arguments.of(new AccessDeniedException("Доступ запрещён"), 403),
                Arguments.of(new EntityNotFoundException("Ресурс не найден"), 404),
                Arguments.of(new IllegalStateException("Конфликт"), 409)
        );
    }

    @Test
    @DisplayName("Удалить мероприятие по ID (Admin)")
    @WithMockUser(username = "admin", roles = "{ADMIN}")
    void eventsIdDelete_WhenEventsExistById_Delete() throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        doNothing().when(eventService).eventsIdDelete(anyLong());
        mockMvc.perform(delete("/admin/events/{id}", id).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest(name = "Удаление бронирования по ID (Admin) -> ошибка {1}")
    @MethodSource("provideExceptionForEventsIdDelete")
    @WithMockUser(username = "admin", roles = "{ADMIN}")
    void eventsIdDelete_WhenExceptionThrown_ThrowsExpectedStatus(RuntimeException exception,
                                                                 int expected) throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        doThrow(exception).when(eventService).eventsIdDelete(anyLong());
        assertDeleteRequestThrows("/admin/events/{id}", id, expected);
    }

    private static Stream<Arguments> provideExceptionForEventsIdDelete() {
        return Stream.of(
                Arguments.of(new IllegalArgumentException("Неверный запрос"), 400),
                Arguments.of(new UserNotAuthenticatedException("Не авторизован"), 401),
                Arguments.of(new AccessDeniedException("Доступ запрещён"), 403),
                Arguments.of(new EntityNotFoundException("Ресурс не найден"), 404),
                Arguments.of(new IllegalStateException("Конфликт"), 409)
        );
    }

    @Test
    @DisplayName("Получить список мероприятий")
    @WithMockUser(username = "user", roles = "{USER}")
    void eventsGet_WhenRequestIsValid_Return200AndEventResponseArray() throws Exception {
        PageableEventResponse response = new PageableEventResponse();
        response.setSize(10);
        response.setNumber(0);
        var from = LocalDateTime.parse("2026-12-12T12:00:00");
        var to = LocalDateTime.parse("2026-12-22T12:00:00");
        when(eventService.eventsGet(any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(response);
        mockMvc.perform(get("/events")
                .param("from", "2026-12-12T12:00:00")
                .param("to", "2026-12-22T12:00:00")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
        verify(eventService).eventsGet(any(), any(), any());
    }

    @ParameterizedTest(name = "Получение мероприятий с невалидными данными: {0}-> 400")
    @CsvSource({
            "from, invalid-date",
            "to, 2026/10/10"
    })
    @WithMockUser(username = "user", roles = "{USER}")
    void eventsGet_WhenRequestIsInvalid_Return400BadRequest(String paramName, String invalidValue) throws Exception {
        mockMvc.perform(get("/events")
                .param(paramName,invalidValue))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(eventService);
    }

    @Test
    @DisplayName("Получить мероприятие -> ошибка 401")
    void eventsGet_WhenNotAuthorized_Throw401() throws Exception {
        when(eventService.eventsGet(any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class))).thenThrow(new UserNotAuthenticatedException("Не авторизован"));
        mockMvc.perform(get("/events"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(eventService);
    }

    @Test
    @DisplayName("Получить мероприятие по ID")
    @WithMockUser(username = "user", roles = "{USER}")
    void eventsIdGet_WhenEventsExistById_Return200AndEventResponse() throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        var response = TestDataFactory.defaultEventResponse();
        when(eventService.eventsIdGet(anyLong())).thenReturn(response);

        mockMvc.perform(get("/events/{id}", id))
                .andExpect(status().isOk());
    }

    @ParameterizedTest(name = "Получить мероприятие по ID -> ошибка {1}")
    @MethodSource("provideExceptionForEventsIdGet")
    @WithMockUser(username = "user", roles = "USER")
    void eventsIdGet_WhenExceptionThrown_ThrowsExpectedStatus(RuntimeException exception,
                                                              int expected) throws Exception {
        Long id = TestDataFactory.DEFAULT_ID;
        when(eventService.eventsIdGet(anyLong())).thenThrow(exception);

        assertGetRequestThrows("/events/{id}", id, expected);
    }

    private  static Stream<Arguments> provideExceptionForEventsIdGet() {
       return Stream.of(
               Arguments.of(new IllegalArgumentException("Неверный запрос"), 400),
               Arguments.of(new EntityNotFoundException("Ресурс не найден"),404)
       );
    }
}