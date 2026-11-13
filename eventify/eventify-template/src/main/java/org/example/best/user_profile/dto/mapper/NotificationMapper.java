package org.example.best.user_profile.dto.mapper;

import org.example.best.user_profile.dto.NotificationPreferences;
import org.example.best.user_profile.model.UserNotificationSetting;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationPreferences toDto(UserNotificationSetting setting);

    void updateEntityFromDto(NotificationPreferences dto,
                             @MappingTarget UserNotificationSetting setting);
}
