package org.example.best.booking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.best.event.dto.response.EventResponse;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Бронирование")
public class BookingResponse {
    @Schema(description = "Идентификатор бронирования", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Идентификатор обязательно должен быть указан")
    private Long id;

    @Schema(description = "Мероприятие")
    private EventResponse event;

    @Schema(description = "Электронная почта пользователя")
    private String customerEmail;

    @Schema(description = "Количество билетов")
    private Integer ticketCount;

    @Schema(description = "Дата создания бронирования")
    private LocalDateTime createdAt;

    @Schema(description = "Дата, до которой бронирование должно быть подтверждено")
    private LocalDateTime expiryTime;

    @Schema(description = "Признак подтверждённого бронирования")
    private Boolean confirmed;

    @Schema(description = "Таймзона")
    private String timezone;



}
