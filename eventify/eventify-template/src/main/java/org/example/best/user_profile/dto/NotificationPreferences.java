package org.example.best.user_profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NotificationPreferences {
    @Schema(description = "Получать уведомление когда добавляются новые мероприятия")
    private Boolean notifyNewEvents;

    @Schema(description = "Получать напоминания о мероприятиях, забронированных пользователем")
    private Boolean notifyUpcoming;

    @Schema(description = "Количество часов до начала мероприятия, за которое будет отправлено уведомление")
    @Min(1)
    @Max(24)
    private Integer notifyBeforeHours;

}
