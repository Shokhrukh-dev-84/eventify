package org.example.best.user_profile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.best.auth.dto.AuthType;
import org.example.best.config.SecurityUtils;
import org.example.best.exception.UserNotAuthenticatedException;
import org.example.best.user_profile.dto.NotificationPreferences;
import org.example.best.user_profile.dto.mapper.NotificationMapper;
import org.example.best.user_profile.model.User;
import org.example.best.user_profile.model.UserNotificationSetting;
import org.example.best.user_profile.repository.NotificationSettingRepository;
import org.example.best.user_profile.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final NotificationMapper mapper;
    private final NotificationSettingRepository settingRepository;

    @Transactional
    public User create(User currentUser) {
        currentUser.setPassword(currentUser.getPassword());

        if (currentUser.getRole() == null) {
            currentUser.setRole(AuthType.USER);
        }

        return userRepository.save(currentUser);
    }

    @Transactional
    public NotificationPreferences userNotificationsGet() {
        User user = currentUser();

        UserNotificationSetting setting = settingRepository.findByUserId(user.getId()).orElseGet(
                () -> settingRepository.save(getSetting(user))
        );
        return mapper.toDto(setting);
    }

    @Transactional
    public NotificationPreferences userNotificationsPut(NotificationPreferences preferences) {
        User user = currentUser();
        UserNotificationSetting setting = settingRepository.findByUserId(user.getId())
                        .orElseGet(() -> getSetting(user));
        setting.setNotifyNewEvents(preferences.getNotifyNewEvents());
        setting.setNotifyUpcoming(preferences.getNotifyUpcoming());
        setting.setNotifyBeforeHours(preferences.getNotifyBeforeHours());

        settingRepository.save(setting);

        log.info("Настройка уведомлений пользователья {} обновлена", user.getUsername());
        return mapper.toDto(setting);
    }

    @Transactional
    public void userNotificationsDelete() {
        User user = currentUser();

        settingRepository.findByUserId(user.getId()).ifPresent(settingRepository::delete);
        log.info("Настройка уведомлений пользователя {} удалена", user.getUsername());
    }

    private UserNotificationSetting getSetting(User user) {
        UserNotificationSetting setting = user.getNotificationSetting();
        if (setting == null) {
            setting = new UserNotificationSetting();
            setting.setUser(user);
            setting.setNotifyNewEvents(true);
            setting.setNotifyUpcoming(true);
            setting.setNotifyBeforeHours(24);

            user.setNotificationSetting(setting);
        }
        return setting;
    }

    private User currentUser() {
        return SecurityUtils.getCurrentUser().orElseThrow(
                () -> new UserNotAuthenticatedException("Пользователь не авторизован")
        );
    }

}
