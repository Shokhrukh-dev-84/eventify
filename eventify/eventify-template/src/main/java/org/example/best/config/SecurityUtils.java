package org.example.best.config;

import org.example.best.auth.dto.AuthType;
import org.example.best.user_profile.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        return Optional.of((User) Objects.requireNonNull(authentication.getPrincipal()));

    }

    public static AuthType getUsersRole() {

        return getCurrentUser()
                .map(User::getRole)
                .orElse(null);
    }
    public static String getUserName() {
        return getCurrentUser()
                .map(User::getUsername)
                .orElse(null);
    }
}
