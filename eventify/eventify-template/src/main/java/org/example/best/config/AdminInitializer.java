package org.example.best.config;

import lombok.RequiredArgsConstructor;
import org.example.best.auth.dto.AuthType;
import org.example.best.user_profile.model.User;
import org.example.best.user_profile.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUserName;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(@NonNull ApplicationArguments args) throws Exception {
        if (!userRepository.existsByEmail(adminUserName)) {
            User admin = new User();
            admin.setEmail(adminUserName);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(AuthType.ADMIN);

            userRepository.save(admin);
        }

    }
}
