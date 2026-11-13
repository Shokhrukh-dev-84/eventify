package org.example.best.auth.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.best.BaseTest;
import org.example.best.TestDataFactory;
import org.example.best.auth.jwt.JwtCore;
import org.example.best.auth.service.AuthService;
import org.example.best.config.CryptoConfig;
import org.example.best.config.SecurityConfig;
import org.example.best.event.controller.EventController;
import org.example.best.exception.UserNotAuthenticatedException;
import org.example.best.user_profile.dto.request.LoginRequest;
import org.example.best.user_profile.dto.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@Import(SpringDataWebConfiguration.class)
class AuthControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtCore jwtCore;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Регистрация пользователя")
    void authRegisterPost_WhenRequestIsValid_Return201AndAuthResponse() throws Exception {
        var request = TestDataFactory.defaultRegisterRequest();
        var response = TestDataFactory.defaultAuthResponse();
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Регистрация пользователя -> ошибка 400")
    void authRegisterPost_WhenRequestIsInvalid_Throw400BadRequest() throws Exception {
        var request = TestDataFactory.defaultRegisterRequest();
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Ошибка валидации логина или пароля"));
        assertPostRequestThrows("/auth/register", request, 400);
    }

    @Test
    @DisplayName("Вход в систему")
    void authLoginPost_WhenRequestIsValid_Return200() throws Exception {
        var response = TestDataFactory.defaultAuthResponse();
        var request = TestDataFactory.defaultLoginRequest();
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(response.getToken()));
    }

    @Test
    @DisplayName("Вход в систему -> ошибка 401")
    void authLoginPost_WhenRequestIsInvalid_Throw401() throws Exception {
        var fakeRequest = TestDataFactory.defaultLoginRequest();
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new UserNotAuthenticatedException("Неверное имя пользователя или пароль"));
        mockMvc.perform(post("/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(fakeRequest)))
                .andExpect(status().isUnauthorized());
    }
}