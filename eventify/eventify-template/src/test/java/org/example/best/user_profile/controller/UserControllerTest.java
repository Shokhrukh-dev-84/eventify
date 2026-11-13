package org.example.best.user_profile.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.best.BaseTest;
import org.example.best.TestDataFactory;
import org.example.best.auth.jwt.JwtCore;
import org.example.best.exception.UserNotAuthenticatedException;
import org.example.best.user_profile.dto.NotificationPreferences;
import org.example.best.user_profile.service.TelegramService;
import org.example.best.user_profile.service.UserService;
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
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@Import(SpringDataWebConfiguration.class)
class UserControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TelegramService telegramService;

    @MockitoBean
    private JwtCore jwtCore;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Получить настройки уведомлений")
    @WithMockUser(username = "user", roles = "{USER}")
    void userNotificationsGet_WhenPreferencesIsValid_Return200AndPreferences() throws Exception {
        var fakePreferences = TestDataFactory.defaultPreferences();
        when(userService.userNotificationsGet()).thenReturn(fakePreferences);

        mockMvc.perform(get("/user/notifications"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получить настройки уведомлений -> ошибка 401")
    @WithMockUser(username = "user", roles = "INVALID_USER")
    void userNotificationsGet_WhenNotAuthorized_Throw401() throws Exception {
        var fakePreferences = TestDataFactory.defaultPreferences();
        when(userService.userNotificationsGet())
                .thenThrow(new UserNotAuthenticatedException("Не авторизован"));
        assertGetRequestThrows("/user/notifications", fakePreferences, 401);
    }

    @Test
    @DisplayName("Обновить настройки уведомлений")
    @WithMockUser(username = "user", roles = "{USER}")
    void userNotificationsPut_WhenRequestIsValid_Return201AndUpdatedPreferences() throws Exception {
        var fakePreferences = TestDataFactory.defaultPreferences();
        var fakeUpdatePreferences = new NotificationPreferences();
        fakeUpdatePreferences.setNotifyUpcoming(true);
        fakeUpdatePreferences.setNotifyNewEvents(false);
        fakeUpdatePreferences.setNotifyBeforeHours(10);
        when(userService.userNotificationsPut(any(NotificationPreferences.class)))
                .thenReturn(fakePreferences);
        mockMvc.perform(put("/user/notifications").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(fakeUpdatePreferences)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest(name = "Обновить настройки уведомлений -> ошибка {1}")
    @MethodSource("provideExceptionForNotificationsPut")
    @WithMockUser(username = "user", roles = "{USER}")
    void userNotificationsPut_WhenExceptionThrown_ThrowsExpectedStatus(RuntimeException exception,
                                                                       int expected) throws Exception {
        var fakePreferences = TestDataFactory.defaultPreferences();
        when(userService.userNotificationsPut(any(NotificationPreferences.class)))
                .thenThrow(exception);
        mockMvc.perform(put("/user/notifications").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(fakePreferences)))
                .andExpect(status().is(expected));
    }

    private static Stream<Arguments> provideExceptionForNotificationsPut() {
        return Stream.of(
                Arguments.of(new UserNotAuthenticatedException("Не авторизован"), 401),
                Arguments.of(new EntityNotFoundException("Настройки уведомлений не найдены"), 404)
        );
    }

    @Test
    @DisplayName("Отменить уведомления")
    @WithMockUser(username = "user", roles = "{USER}")
    void userNotificationsDelete_WhenNotificationIsExist_Return204() throws Exception {
        doNothing().when(userService).userNotificationsDelete();
        mockMvc.perform(delete("/user/notifications"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Отменить уведомление -> ошибка 401")
    @WithMockUser(username = "user", roles = "{USER}")
    void userNotificationsDelete_WhenNotAuthorized_Throw401() throws Exception {
        doThrow(new UserNotAuthenticatedException("Не авторизован"))
                .when(userService).userNotificationsDelete();
        mockMvc.perform(delete("/user/notifications"))
                .andExpect(status().isUnauthorized());
    }
}