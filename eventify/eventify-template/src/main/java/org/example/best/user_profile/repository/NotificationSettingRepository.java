package org.example.best.user_profile.repository;

import org.example.best.user_profile.model.User;
import org.example.best.user_profile.model.UserNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<UserNotificationSetting, Long> {
    Optional<UserNotificationSetting> findByUserId(Long userId);
}
