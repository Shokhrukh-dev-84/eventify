package org.example.best.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.best.auth.dto.response.AuthResponse;
import org.example.best.auth.jwt.JwtCore;
import org.example.best.exception.UserNotAuthenticatedException;
import org.example.best.user_profile.dto.request.LoginRequest;
import org.example.best.user_profile.dto.request.RegisterRequest;
import org.example.best.user_profile.model.User;
import org.example.best.user_profile.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userService.create(user);
        List<String> roles = savedUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        String token = jwtCore.generateToken(savedUser.getUsername(), roles);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRole(savedUser.getRole());
        log.info("Пользователь с email: {}, успешно зарегистрирован", request.getEmail());
        return response;
    }

    public AuthResponse login(LoginRequest request) {
        try
        {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();

            List<String> roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String token = jwtCore.generateToken(request.getEmail(), roles);

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setRole(user.getRole());
            log.info("Пользователь {} успешно авторизован", request.getEmail());
            return response;
        } catch (Exception e) {
            log.warn("Failed login attempt for user {}: {}",request.getEmail(), e.getMessage());
            throw new UserNotAuthenticatedException("Логин или пароль неверно указан");

        }
    }
}
