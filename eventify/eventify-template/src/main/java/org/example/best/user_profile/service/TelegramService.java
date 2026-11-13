package org.example.best.user_profile.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.best.config.SecurityUtils;
import org.example.best.user_profile.model.User;
import org.example.best.user_profile.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {
    private final UserRepository repository;

    @Transactional
    public String getTelegramLink() {
        String linkToken = UUID.randomUUID().toString();
        String currentUserEmail = SecurityUtils.getUserName();
        User currentUser = repository.findByEmail(currentUserEmail).orElseThrow(
                () -> new EntityNotFoundException("Пользеватель с email: " + currentUserEmail + " не найден")
        );

        currentUser.setTelegramLinkToken(linkToken);

        repository.save(currentUser);
        return linkToken;
    }
}
